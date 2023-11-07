import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import {
  mockDoalApplicationSubmitRequestTaskItem,
  updateMockDoalApplicationSubmitRequestTaskItem,
} from '@tasks/doal/test/mock';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { initialState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { CompleteComponent } from './complete.component';

describe('CompleteComponent', () => {
  let component: CompleteComponent;
  let fixture: ComponentFixture<CompleteComponent>;
  let hostElement: HTMLElement;

  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub({
    taskId: `${mockDoalApplicationSubmitRequestTaskItem.requestTask.id}`,
  });

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CompleteComponent> {
    get completeButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CompleteComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompleteComponent],
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
      requestTaskItem: updateMockDoalApplicationSubmitRequestTaskItem({
        ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
        determination: {
          type: 'CLOSED',
          reason: 'reason',
        },
      }),
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should complete', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    page.completeButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'DOAL_CLOSE_APPLICATION',
      requestTaskId: mockDoalApplicationSubmitRequestTaskItem.requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    fixture.detectChanges();
    expect(hostElement.textContent.trim()).toEqual(
      'Task completedYour reference code is:DOAL12345-2025-1  Return to dashboard',
    );
  });
});
