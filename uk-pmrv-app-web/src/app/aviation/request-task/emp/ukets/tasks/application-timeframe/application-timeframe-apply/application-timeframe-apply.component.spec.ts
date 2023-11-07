import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpApplicationTimeframeInfo, TasksService } from 'pmrv-api';

import { ApplicationTimeframeFormProvider } from '../application-timeframe-form.provider';
import { applicationTimeframeInfoQuery } from '../store/application-timeframe.selectors';
import { ApplicationTimeframeApplyComponent } from './application-timeframe-apply.component';

describe('ApplicationTimeframeApplyComponent', () => {
  let component: ApplicationTimeframeApplyComponent;
  let fixture: ComponentFixture<ApplicationTimeframeApplyComponent>;
  let store: RequestTaskStore;
  let formProvider: ApplicationTimeframeFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ApplicationTimeframeFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<ApplicationTimeframeFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(ApplicationTimeframeApplyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const data = {
      applicationTimeframeInfo: {
        ...store.empUkEtsDelegate.payload.emissionsMonitoringPlan.applicationTimeframeInfo,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empUkEtsDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue({
      dateOfStart: new Date('2018-01-01'),
      submittedOnTime: false,
      reasonForLateSubmission: 'The reason for late submission',
    });
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    const storeData = await firstValueFrom(store.pipe(applicationTimeframeInfoQuery.selectApplicationTimeframeInfo));
    expect(storeData).toEqual(data.applicationTimeframeInfo);

    saveEmpSpy.mockRestore();
  });
});

function setupStore(store: RequestTaskStore, formProvider: ApplicationTimeframeFormProvider) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 19,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        daysRemaining: 6,
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          payloadType: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD',
          emissionsMonitoringPlan: {
            applicationTimeframeInfo: {
              dateOfStart: new Date('2018-01-01'),
              submittedOnTime: false,
              reasonForLateSubmission: 'The reason for late submission',
            },
          },
          empSectionsCompleted: {
            applicationTimeframeInfo: [false],
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

  formProvider.setFormValue(
    store.empUkEtsDelegate.payload.emissionsMonitoringPlan.applicationTimeframeInfo as EmpApplicationTimeframeInfo,
  );
}
