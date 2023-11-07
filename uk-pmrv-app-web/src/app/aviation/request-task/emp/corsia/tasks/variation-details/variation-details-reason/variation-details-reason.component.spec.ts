import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { VariationDetailsReasonComponent } from '@aviation/request-task/emp/corsia/tasks/variation-details/variation-details-reason/variation-details-reason.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpVariationCorsiaDetails, TasksService } from 'pmrv-api';

import { empQuery } from '../../../../shared/emp.selectors';
import { VariationDetailsFormProvider } from '../variation-details-form.provider';

describe('VariationDetailsReasonComponent', () => {
  let component: VariationDetailsReasonComponent;
  let fixture: ComponentFixture<VariationDetailsReasonComponent>;
  let store: RequestTaskStore;
  let formProvider: VariationDetailsFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: VariationDetailsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: {
            payloadType: 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD',
            emissionsMonitoringPlan: {},
            empSectionsCompleted: {},
            empVariationDetailsCompleted: false,
            empVariationDetails: {
              changes: ['FUMM_TO_ESTIMATION_METHOD', 'REGISTERED_OFFICE_ADDRESS'],
              reason: 'test reason',
            },
            reasonRegulatorLed: {
              type: 'FOLLOWING_IMPROVING_REPORT',
              reasonOtherSummary: null,
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
        empVariationDetails: { status: 'not started' },
      },
    } as any);

    formProvider = TestBed.inject<VariationDetailsFormProvider>(TASK_FORM_PROVIDER);
    formProvider.setFormValue(
      store.empCorsiaDelegate.payload.empVariationDetails as EmpVariationCorsiaDetails,
      store.empCorsiaDelegate.payload.reasonRegulatorLed,
    );

    fixture = TestBed.createComponent(VariationDetailsReasonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const data = {
      reasonRegulatorLed: store.empCorsiaDelegate.payload.reasonRegulatorLed,
    };

    const saveEmpSpy = jest.spyOn(store.empCorsiaDelegate, 'saveEmp').mockReturnValue(of({}));

    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    const storeData = await firstValueFrom(store.pipe(empQuery.selectVariationRegulatorLedReason));
    expect(storeData).toEqual(data.reasonRegulatorLed);

    saveEmpSpy.mockRestore();
  });
});
