import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpBlockOnBlockOffMethodProcedures, TasksService } from 'pmrv-api';

import { BlockProceduresFormProvider } from '../block-procedures-form.provider';
import { blockProceduresQuery } from '../store/block-procedures.selectors';
import { BlockProceduresMonitoringComponent } from './block-procedures-monitoring.component';

describe('BlockProceduresMonitoringComponent', () => {
  let component: BlockProceduresMonitoringComponent;
  let fixture: ComponentFixture<BlockProceduresMonitoringComponent>;
  let store: RequestTaskStore;
  let formProvider: BlockProceduresFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<BlockProceduresFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(BlockProceduresMonitoringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const data = {
      blockOnBlockOffMethodProcedures: {
        fuelConsumptionPerFlight: {
          ...store.empDelegate.payload.emissionsMonitoringPlan.blockOnBlockOffMethodProcedures.fuelConsumptionPerFlight,
        },
      },
    };

    const saveEmpSpy = jest.spyOn(store.empDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue({
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
      locationOfRecords: 'Location of records',
      itSystemUsed: 'IT system used',
    });

    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');

    const storeData = await firstValueFrom(store.pipe(blockProceduresQuery.selectBlockProcedures));
    expect(storeData).toEqual(data.blockOnBlockOffMethodProcedures.fuelConsumptionPerFlight);

    saveEmpSpy.mockRestore();
  });
});

function setupStore(store: RequestTaskStore, formProvider: BlockProceduresFormProvider) {
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
            blockOnBlockOffMethodProcedures: {
              fuelConsumptionPerFlight: {
                procedureDescription: 'Procedure description',
                procedureDocumentName: 'Name of the procedure document',
                procedureReference: 'Procedure reference',
                responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
                locationOfRecords: 'Location of records',
                itSystemUsed: 'IT system used',
              },
            },
          },
          empSectionsCompleted: {
            blockOnBlockOffMethodProcedures: [false],
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
    store.empDelegate.payload.emissionsMonitoringPlan
      .blockOnBlockOffMethodProcedures as EmpBlockOnBlockOffMethodProcedures,
  );
}
