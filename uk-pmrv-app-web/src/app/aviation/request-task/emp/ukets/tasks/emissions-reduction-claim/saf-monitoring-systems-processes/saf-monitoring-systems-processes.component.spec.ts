import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { SafMonitoringSystemsProcessesComponent } from './saf-monitoring-systems-processes.component';

describe('SafMonitoringSystemsProcessesComponent', () => {
  let component: SafMonitoringSystemsProcessesComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).addProcedureForms();

    component = TestBed.createComponent(SafMonitoringSystemsProcessesComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('SAF monitoring systems and processes')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(screen.getByText('the purchase and delivery records you expect to obtain or maintain')).toBeInTheDocument();
    expect(
      screen.getByText(
        'any processes you will use for tracking the batches of SAF through the fuel supply chain (for example product transfer documentation, invoices, delivery notes, internal tracking system)',
      ),
    ).toBeInTheDocument();
    expect(screen.getByText('any information from your fuel supplier that you expect to submit')).toBeInTheDocument();
  });
});
