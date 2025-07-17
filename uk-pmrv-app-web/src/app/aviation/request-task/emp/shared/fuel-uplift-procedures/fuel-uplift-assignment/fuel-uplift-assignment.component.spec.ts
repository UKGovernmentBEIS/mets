import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { FuelUpliftAssignmentComponent } from './fuel-uplift-assignment.component';

describe('FuelUpliftAssignmentComponent', () => {
  let component: FuelUpliftAssignmentComponent;
  let fixture: ComponentFixture<FuelUpliftAssignmentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FuelUpliftProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        provideHttpClient(withInterceptorsFromDi()),
        provideRouter([]),
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
          daysRemaining: 6,
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: null,
        },
        requestInfo: { type: 'EMP_ISSUANCE_UKETS' },
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(FuelUpliftAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
