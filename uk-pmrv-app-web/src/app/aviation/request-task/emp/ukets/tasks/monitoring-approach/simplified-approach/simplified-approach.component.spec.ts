import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { EmissionsMonitoringApproach } from '@aviation/shared/components/emp/monitoring-approach-summary-template/monitoring-approach-types.interface';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { MonitoringApproachFormProvider } from '../monitoring-approach-form.provider';
import { monitoringApproachQuery } from '../store/monitoring-approach.selectors';
import { SimplifiedApproachComponent } from './simplified-approach.component';

describe('SimplifiedApproachComponent', () => {
  let component: SimplifiedApproachComponent;
  let fixture: ComponentFixture<SimplifiedApproachComponent>;
  let store: RequestTaskStore;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);
  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    setupStore(store, TestBed.inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER));

    fixture = TestBed.createComponent(SimplifiedApproachComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('MonitoringApproachComponent', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should call the saveEmp function with the correct data when the form is valid', async () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const data = {
        emissionsMonitoringApproach: {
          monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
          explanation: 'test',
          supportingEvidenceFiles: [],
        },
      };

      const saveEmpSpy = jest.spyOn(store.empUkEtsDelegate, 'saveEmp').mockReturnValue(of({}));

      component.form.setValue({
        explanation: 'test',
        supportingEvidenceFiles: [],
      });
      component.onSubmit();

      expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');
      const storeData = await firstValueFrom(store.pipe(monitoringApproachQuery.selectMonitoringApproach));
      expect(storeData).toEqual(data.emissionsMonitoringApproach);

      saveEmpSpy.mockRestore();
    });
  });
});

function setupStore(store: RequestTaskStore, formProvider: MonitoringApproachFormProvider) {
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
            emissionsMonitoringApproach: {
              monitoringApproachType: 'EUROCONTROL_SUPPORT_FACILITY',
              explanation: 'test',
              supportingEvidenceFiles: null,
            },
          },
          empSectionsCompleted: {
            emissionsMonitoringApproach: [false],
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
    store.empUkEtsDelegate.payload.emissionsMonitoringPlan.emissionsMonitoringApproach as EmissionsMonitoringApproach,
  );
}
