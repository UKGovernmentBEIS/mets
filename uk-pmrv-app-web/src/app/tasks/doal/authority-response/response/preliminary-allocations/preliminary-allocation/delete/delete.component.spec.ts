import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { DeleteComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/delete/delete.component';
import { DoalTaskComponent } from '@tasks/doal/shared/components/doal-task/doal-task.component';
import { mockDoalAuthorityResponseRequestTaskTaskItem } from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { DoalAuthorityResponseRequestTaskPayload, TasksService } from 'pmrv-api';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;

  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub({
    taskId: `${mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id}`,
    index: '1',
  });

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
      declarations: [DeleteComponent, DoalTaskComponent],
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
      requestTaskItem: mockDoalAuthorityResponseRequestTaskTaskItem,
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
      requestTaskActionType: 'DOAL_SAVE_AUTHORITY_RESPONSE',
      requestTaskId: mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
        doalAuthority: {
          ...(
            mockDoalAuthorityResponseRequestTaskTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload
          ).doalAuthority,
          authorityResponse: {
            ...(
              mockDoalAuthorityResponseRequestTaskTaskItem.requestTask
                .payload as DoalAuthorityResponseRequestTaskPayload
            ).doalAuthority.authorityResponse,
            preliminaryAllocations: [
              {
                year: 2024,
                allowances: 200,
                subInstallationName: 'ALUMINIUM',
              },
            ],
          },
        },
        doalSectionsCompleted: {
          authorityResponse: false,
          dateSubmittedToAuthority: true,
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
