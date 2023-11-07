import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { MonitoringPlanChangesFormProvider } from '../monitoring-plan-changes-form.provider';
import { MonitoringPlanChangesSummaryComponent } from './monitoring-plan-changes-summary.component';

describe('MonitoringPlanChangesSummaryComponent', () => {
  let component: MonitoringPlanChangesSummaryComponent;
  let fixture: ComponentFixture<MonitoringPlanChangesSummaryComponent>;
  let store: RequestTaskStore;
  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringPlanChangesFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store);
    fixture = TestBed.createComponent(MonitoringPlanChangesSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

function setupStore(store: RequestTaskStore) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 1,
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
        daysRemaining: 6,
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          aerMonitoringPlanVersions: [],
          aerSectionsCompleted: {},
        } as AerRequestTaskPayload,
      },
      requestInfo: {
        id: '2',
        type: 'AVIATION_AER_UKETS',
      },
    },
    timeline: [],
    relatedTasks: [
      {
        taskId: 2,
      },
    ],
    taskReassignedTo: null,
    isTaskReassigned: false,
    isEditable: true,
    tasksState: {},
  });
}
