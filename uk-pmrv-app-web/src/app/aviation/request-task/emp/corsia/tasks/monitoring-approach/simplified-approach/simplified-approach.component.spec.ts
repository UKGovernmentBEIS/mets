import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store/request-task.types';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { MonitoringApproachCorsiaFormProvider } from '../monitoring-approach-form.provider';
import { monitoringApproachCorsiaQuery } from '../store/monitoring-approach.selectors';
import { SimplifiedApproachComponent } from './simplified-approach.component';

describe('SimplifiedApproachComponent', () => {
  let component: SimplifiedApproachComponent;
  let fixture: ComponentFixture<SimplifiedApproachComponent>;
  let store: RequestTaskStore;
  let formProvider: MonitoringApproachCorsiaFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<MonitoringApproachCorsiaFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(SimplifiedApproachComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const data = {
      emissionsMonitoringApproach: {
        ...(store.empCorsiaDelegate.payload as EmpRequestTaskPayloadCorsia).emissionsMonitoringPlan
          .emissionsMonitoringApproach,
        monitoringApproachType: 'CERT_MONITORING',
        certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
        explanation: 'explanation text',
        supportingEvidenceFiles: [],
      },
    };

    const saveEmpSpy = jest.spyOn(store.empCorsiaDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue({
      explanation: 'explanation text',
      supportingEvidenceFiles: [],
    } as any);
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
    const storeData = await firstValueFrom(store.pipe(monitoringApproachCorsiaQuery.selectMonitoringApproachCorsia));
    expect(storeData).toEqual(data.emissionsMonitoringApproach);

    saveEmpSpy.mockRestore();
  });
});

function setupStore(store: RequestTaskStore, formProvider: MonitoringApproachCorsiaFormProvider) {
  store.setState({
    requestTaskItem: {
      requestTask: {
        id: 19,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
        daysRemaining: 6,
        assigneeFullName: 'TEST_ASSIGNEE',
        payload: {
          payloadType: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD',
          emissionsMonitoringPlan: {
            emissionsMonitoringApproach: {
              monitoringApproachType: 'CERT_MONITORING',
              explanation: 'explanation text',
              supportingEvidenceFiles: [],
              certEmissionsType: 'GREAT_CIRCLE_DISTANCE',
            },
          },
          empSectionsCompleted: {
            emissionsMonitoringApproach: [false],
          },
          empAttachments: {
            '03044d41-7aa2-46f6-beb1-9d0cfaf466b1': 'fileUpload.txt',
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
    (store.empCorsiaDelegate.payload as any).emissionsMonitoringPlan.emissionsMonitoringApproach,
  );
}
