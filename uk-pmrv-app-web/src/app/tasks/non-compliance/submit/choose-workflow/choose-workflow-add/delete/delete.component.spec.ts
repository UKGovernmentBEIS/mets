import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { TaskSharedModule } from '../../../../../shared/task-shared-module';
import { initialState } from '../../../../../store/common-tasks.state';
import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { NonComplianceTaskComponent } from '../../../../shared/components/non-compliance-task/non-compliance-task.component';
import {
  mockCompletedNonComplianceApplicationSubmitRequestTaskItem,
  updateMockedNonCompliance,
} from '../../../../test/mock';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub({ index: '0' });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeleteComponent> {
    get deleteButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteComponent, NonComplianceTaskComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...initialState,
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: updateMockedNonCompliance({ selectedRequests: ['item1'] }, false),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    page.deleteButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'NON_COMPLIANCE_SAVE_APPLICATION',
      requestTaskId: mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask.payload,
        payloadType: 'NON_COMPLIANCE_SAVE_APPLICATION_PAYLOAD',
        selectedRequests: [],
        sectionCompleted: false,
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
