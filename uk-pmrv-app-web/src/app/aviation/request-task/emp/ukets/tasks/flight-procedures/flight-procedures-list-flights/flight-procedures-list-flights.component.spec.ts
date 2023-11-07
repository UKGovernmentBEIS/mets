import { FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { render, RenderResult } from '@testing-library/angular';

import { EmpFlightAndAircraftProcedures } from 'pmrv-api';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import { onSubmitFlightProcedures } from '../shared/flight-procedures.functions';
import { FlightProceduresListFlightsComponent } from './flight-procedures-list-flights.component';

describe('FlightProceduresCoveredUkComponent', () => {
  let store: RequestTaskStore;
  let form: FormGroup;
  let flightAndAircraftProcedures: EmpFlightAndAircraftProcedures;
  let pageFieldForm: string;
  let nextStep: string;
  let pendingRequestService: PendingRequestService;
  let result: RenderResult<FlightProceduresListFlightsComponent>;

  beforeEach(() => {
    form = {
      value: {
        procedureDescription: 'Procedure description',
        procedureDocumentName: 'Name of the procedure document',
        procedureReference: 'Procedure reference',
        responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
        locationOfRecords: 'Location of records',
        itSystemUsed: 'IT system used',
      },
      valid: true,
    } as any;

    flightAndAircraftProcedures = {
      aircraftUsedDetails: form.value,
      flightListCompletenessDetails: form.value,
      ukEtsFlightsCoveredDetails: form.value,
    } as any;

    store = {
      empUkEtsDelegate: {
        saveEmp: jest.fn(),
        setFlightProcedures: jest.fn(),
        payload: { emissionsMonitoringPlan: { flightAndAircraftProcedures } },
      } as any,
    } as any;

    pageFieldForm = 'flightListCompletenessDetails';
    nextStep = 'flights-covered-by-the-UK-ETS';

    pendingRequestService = {
      trackRequest: jest.fn(() => ({
        subscribe: jest.fn(),
      })),
    } as any;
  });

  beforeEach(async () => {
    result = await render(FlightProceduresListFlightsComponent, {
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });
  });

  it('should create', () => {
    const {
      fixture: { componentInstance: component },
    } = result;

    expect(component).toBeTruthy();
  });

  it('should call the necessary functions of onSubmitFlightProcedures with the correct parameters when the form is valid', () => {
    onSubmitFlightProcedures(store, form, pageFieldForm, nextStep, {} as any, {} as any, pendingRequestService);

    expect(store?.empUkEtsDelegate?.saveEmp).toHaveBeenCalledWith(
      {
        flightAndAircraftProcedures: { ...flightAndAircraftProcedures, aircraftUsedDetails: form.value },
      },
      'in progress',
    );

    expect(pendingRequestService?.trackRequest).toHaveBeenCalled();
  });
});
