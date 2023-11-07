import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach, ProcedureOptionalForm, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { TaskProcedureOptionalFormPipe } from './task-procedure-optional-form.pipe';

describe('TaskProcedureOptionalFormPipe', () => {
  let pipe: TaskProcedureOptionalFormPipe;
  let store: PermitApplicationStore<PermitApplicationState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskProcedureOptionalFormPipe],
      providers: [
        { provide: TasksService, useValue: mockClass(TasksService) },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new TaskProcedureOptionalFormPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an observable of the task procedure optional form', async () => {
    const biomassEmissions: ProcedureOptionalForm = {
      exist: true,
      procedureForm: {
        locationOfRecords: 'fff',
        procedureDescription: 'fff',
        procedureDocumentName: 'fff',
        procedureReference: 'fff',
        responsibleDepartmentOrRole: 'fff',
      },
    };

    store.setState({
      ...store.getState(),
      ...mockState,
      permit: {
        ...mockState.permit,
        monitoringApproaches: {
          ...mockPermitApplyPayload.permit.monitoringApproaches,
          MEASUREMENT_CO2: {
            ...mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2,
            biomassEmissions,
          } as MeasurementOfCO2MonitoringApproach,
        },
      },
    });

    await expect(
      firstValueFrom(pipe.transform('monitoringApproaches.MEASUREMENT_CO2.biomassEmissions')),
    ).resolves.toEqual(biomassEmissions);
  });
});
