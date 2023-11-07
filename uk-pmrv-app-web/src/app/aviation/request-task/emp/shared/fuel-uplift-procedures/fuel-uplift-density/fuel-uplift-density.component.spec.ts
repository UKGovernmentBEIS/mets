import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ActivatedRouteStub, mockClass } from '@testing';

import { EmpFuelUpliftMethodProcedures, TasksService } from 'pmrv-api';

import { FuelUpliftProceduresFormProvider } from '../fuel-uplift-procedures-form.provider';
import { fuelUpliftProceduresQuery } from '../store/fuel-uplift-procedures.selectors';
import { FuelUpliftDensityComponent } from './fuel-uplift-density.component';

const formProcedureValues = {
  procedureDescription: 'Procedure description',
  procedureDocumentName: 'Name of the procedure document',
  procedureReference: 'Procedure reference',
  responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
  locationOfRecords: 'Location of records',
  itSystemUsed: 'IT system used',
};

const fuelForm = {
  blockHoursPerFlight: formProcedureValues,
  zeroFuelUplift: 'description',
  fuelUpliftSupplierRecordType: 'FUEL_DELIVERY_NOTES',
  fuelDensity: formProcedureValues,
};

describe('FuelUpliftDensityComponent', () => {
  let component: FuelUpliftDensityComponent;
  let fixture: ComponentFixture<FuelUpliftDensityComponent>;
  let store: RequestTaskStore;
  let formProvider: FuelUpliftProceduresFormProvider;
  const tasksService = mockClass(TasksService);
  const requestTaskFileService = mockClass(RequestTaskFileService);

  const activatedRouteStub = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FuelUpliftProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    formProvider = TestBed.inject<FuelUpliftProceduresFormProvider>(TASK_FORM_PROVIDER);
    setupStore(store, formProvider);

    fixture = TestBed.createComponent(FuelUpliftDensityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call the saveEmp function with the correct data when the form is valid', async () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    const data = {
      fuelUpliftMethodProcedures: {
        ...store.empDelegate.payload.emissionsMonitoringPlan.fuelUpliftMethodProcedures,
      },
    };

    const saveEmpSpy = jest.spyOn(store.empDelegate, 'saveEmp').mockReturnValue(of({}));

    component.form.setValue(formProcedureValues);
    component.onSubmit();

    expect(saveEmpSpy).toHaveBeenCalledWith(data, 'in progress');

    const storeData = await firstValueFrom(store.pipe(fuelUpliftProceduresQuery.selectFuelUpliftProcedures));
    expect(storeData).toEqual(data.fuelUpliftMethodProcedures);

    saveEmpSpy.mockRestore();
  });
});

function setupStore(store: RequestTaskStore, formProvider: FuelUpliftProceduresFormProvider) {
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
            fuelUpliftMethodProcedures: fuelForm,
          },
          empSectionsCompleted: {
            fuelUpliftMethodProcedures: [false],
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
    store.empDelegate.payload.emissionsMonitoringPlan.fuelUpliftMethodProcedures as EmpFuelUpliftMethodProcedures,
  );
}
