import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { VariationDetailsFormProvider } from '../variation-details-form.provider';
import { VariationDetailsSummaryComponent } from './variation-details-summary.component';

describe('VariationDetailsSummaryComponent', () => {
  let component: VariationDetailsSummaryComponent;
  let fixture: ComponentFixture<VariationDetailsSummaryComponent>;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VariationDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT',
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: {
            payloadType: 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT_PAYLOAD',
            emissionsMonitoringPlan: {},
            empSectionsCompleted: {},
            empVariationDetailsCompleted: false,
            empVariationDetails: {
              changes: ['FUMM_TO_ESTIMATION_METHOD', 'REGISTERED_OFFICE_ADDRESS'],
              reason: 'test reason',
            },
          },
        },
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: false,
      tasksState: {
        abbreviations: { status: 'not started' },
      },
    } as any);

    fixture = TestBed.createComponent(VariationDetailsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
