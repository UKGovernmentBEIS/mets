import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { MethodAProceduresFormProvider } from '../method-a-procedures-form.provider';
import { MethodAProceduresFuelConsumptionComponent } from './method-a-procedures-fuel-consumption.component';

describe('MethodAProceduresFuelConsumptionComponent', () => {
  let component: MethodAProceduresFuelConsumptionComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodAProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<MethodAProceduresFormProvider>(TASK_FORM_PROVIDER).form;

    component = TestBed.createComponent(MethodAProceduresFuelConsumptionComponent).componentInstance;
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
        'FN = fuel consumed for the flight under consideration (flight = N) determined using Method A (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'TN = amount of fuel contained in aeroplane tanks once fuel uplifts for the flight under consideration (flight N) are complete (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'TN+1 = amount of fuel contained in aeroplane tanks once fuel uplifts for the subsequent flight (flight N+1) are complete (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'UN+1 = sum of fuel uplifts for the subsequent flight (flight N+1) measured in volume and multiplied with a density value (in tonnes)',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'Fuel uplift UN+1 is determined by the measurement by the fuel supplier, as documented in the fuel delivery notes or invoices for each flight.',
      ),
    ).toBeInTheDocument();
  });
});
