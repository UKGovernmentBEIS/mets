import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { defer, of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { cloneDeep } from 'lodash-es';

import { FlightProceduresFormProvider } from '../flight-procedures-form.provider';
import { FlightProceduresSummaryComponent } from './flight-procedures-summary.component';

describe('FlightProceduresSummaryComponent', () => {
  let component: FlightProceduresSummaryComponent;
  let fixture: ComponentFixture<FlightProceduresSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        PendingRequestService,
        { provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlightProceduresSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call store.empDelegate.saveEmp on submit when form is valid', () => {
    const values = {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      responsibleDepartmentOrRole: 'Department or role responsible for data maintenance',
      locationOfRecords: 'Location of records',
      itSystemUsed: 'IT system used',
    };

    component.form = (component as any).formProvider.buildForm();

    const data = {
      aircraftUsedDetails: values,
      flightListCompletenessDetails: values,
      ukEtsFlightsCoveredDetails: values,
    };

    component.form.patchValue(data);

    const store = {
      empUkEtsDelegate: {
        saveEmp: jest.fn(() => {
          return defer(() => of({}));
        }),
        setFlightProcedures: jest.fn(),
      },
    };

    const pendingRequestService = {
      trackRequest: jest.fn(),
    };

    (component as any).flightAndAircraftProceduresToStore = data;

    component['store'] = store as any;

    (component as any)['pendingRequestService'] = pendingRequestService as any;

    component.onSubmit();

    expect(store.empUkEtsDelegate.saveEmp).toHaveBeenCalledWith(
      cloneDeep({ flightAndAircraftProcedures: data }),
      'complete',
    );

    expect(pendingRequestService.trackRequest).toHaveBeenCalled();
  });
});
