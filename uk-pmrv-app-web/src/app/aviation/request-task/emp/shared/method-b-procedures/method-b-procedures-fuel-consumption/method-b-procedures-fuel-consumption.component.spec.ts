import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { MethodBProceduresFormProvider } from '../method-b-procedures-form.provider';
import { MethodBProceduresFuelConsumptionComponent } from './method-b-procedures-fuel-consumption.component';

describe('MethodBProceduresFuelConsumptionComponent', () => {
  let component: MethodBProceduresFuelConsumptionComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodBProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER).form;

    component = TestBed.createComponent(MethodBProceduresFuelConsumptionComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Monitoring fuel consumption per flight')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText('Describe how you monitor fuel consumption per flight in owned and leased-in aircraft.'),
    ).toBeInTheDocument();

    expect(screen.getByText('You should include:')).toBeInTheDocument();
    expect(screen.getByText('the exact time when the fuel in tank measurements are taken')).toBeInTheDocument();
    expect(screen.getByText('a description of the measurement equipment')).toBeInTheDocument();

    expect(
      screen.getByText('the procedures for recording, retrieving, transmitting and storing information'),
    ).toBeInTheDocument();

    expect(screen.getByText('how fuel uplift is determined')).toBeInTheDocument();

    expect(
      screen.getByText(
        'FN = fuel consumed for the flight under consideration (flight = N) determined using Method B (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'RN-1 = amount of fuel remaining in aeroplane tanks at the end of the previous flight (flight N-1) at Block-on before the flight under consideration (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'RN = amount of fuel remaining in aeroplane tanks at the end of the flight under consideration (flight N) at Block-on after the flight (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'UN = fuel uplift for the flight considered, measured in volume and multiplied with a density value (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'Fuel uplift is determined by the measurement by the fuel supplier, as documented in the fuel delivery notes or invoices for each flight.',
      ),
    ).toBeInTheDocument();
  });
});
