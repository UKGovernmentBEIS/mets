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
import { SafDuplicationPreventionComponent } from './saf-duplication-prevention.component';

describe('SafDuplicationPreventionComponent', () => {
  let component: SafDuplicationPreventionComponent;

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

    component = TestBed.createComponent(SafDuplicationPreventionComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Avoid double counting of SAF')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText('used to obtain a benefit from another greenhouse gas emissions reduction or regulatory scheme'),
    ).toBeInTheDocument();
    expect(screen.getByText('sold to a third party')).toBeInTheDocument();
    expect(
      screen.getByText('If you report to another emissions reduction scheme, please name this.'),
    ).toBeInTheDocument();
  });
});
