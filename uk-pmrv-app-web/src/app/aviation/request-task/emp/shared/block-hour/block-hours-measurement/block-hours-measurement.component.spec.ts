import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpBlockHourMethodProcedures, TasksService } from 'pmrv-api';

import { BlockHourProceduresFormProvider } from '../block-hour-procedures-form.provider';
import { blockHourProceduresQuery } from '../store/block-hour-procedures.selectors';
import { BlockHoursMeasurementComponent } from './block-hours-measurement.component';

const formProcedureValues = {
  procedureDescription: 'Procedure description',
  procedureDocumentName: 'Name of the procedure document',
  procedureReference: 'Procedure reference',
  responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
  locationOfRecords: 'Location of records',
  itSystemUsed: 'IT system used',
};

const fuelForm = {
  fuelBurnCalculationTypes: ['NOT_CLEAR_DISTINGUISHION'],
  clearDistinguishionIcaoAircraftDesignators: null,
  notClearDistinguishionIcaoAircraftDesignators: ['All'],
  blockHoursMeasurement: formProcedureValues,
  fuelUpliftSupplierRecordType: 'FUEL_DELIVERY_NOTES',
  fuelDensity: formProcedureValues,
};

describe('BlockHoursMeasurementComponent', () => {
  let component: BlockHoursMeasurementComponent;
  let fixture: ComponentFixture<BlockHoursMeasurementComponent>;
  let store: RequestTaskStore;
  let formProvider: BlockHourProceduresFormProvider;
  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<BlockHourProceduresFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(BlockHoursMeasurementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const data = {
      blockHourMethodProcedures: {
        ...store.empDelegate.payload.emissionsMonitoringPlan.blockHourMethodProcedures,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue(formProcedureValues);
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');

    const storeData = await firstValueFrom(store.pipe(blockHourProceduresQuery.selectBlockHourProcedures));
    expect(storeData).toEqual(data.blockHourMethodProcedures);

    saveEmpSpy.mockRestore();
  });
});

function setupStore(store: RequestTaskStore, formProvider: BlockHourProceduresFormProvider) {
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
            blockHourMethodProcedures: fuelForm,
          },
          empSectionsCompleted: {
            blockHourMethodProcedures: [false],
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
    store.empDelegate.payload.emissionsMonitoringPlan.blockHourMethodProcedures as EmpBlockHourMethodProcedures,
  );
}
