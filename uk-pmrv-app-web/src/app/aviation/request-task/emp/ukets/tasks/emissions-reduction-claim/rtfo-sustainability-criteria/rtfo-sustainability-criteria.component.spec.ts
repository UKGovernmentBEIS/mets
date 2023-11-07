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
import { RtfoSustainabilityCriteriaComponent } from './rtfo-sustainability-criteria.component';

describe('RtfoSustainabilityCriteriaComponent', () => {
  let component: RtfoSustainabilityCriteriaComponent;

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

    component = TestBed.createComponent(RtfoSustainabilityCriteriaComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(
      screen.getByText('Meet the Renewable Transport Fuel Obligation (RTFO) sustainability criteria'),
    ).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(screen.getByText('information from your fuel supplier')).toBeInTheDocument();
    expect(screen.getByText('information on documents related to purchase or delivery')).toBeInTheDocument();
    expect(screen.getByText('screen shots from the RTFO operating system')).toBeInTheDocument();
    expect(screen.getByText('Proof of Sustainability certificates, Proof of Compliance documents')).toBeInTheDocument();
  });
});
