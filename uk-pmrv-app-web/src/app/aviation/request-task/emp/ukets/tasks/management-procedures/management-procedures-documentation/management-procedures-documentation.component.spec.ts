import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';
import { screen } from '@testing-library/dom';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresDocumentationComponent } from './management-procedures-documentation.component';

describe('ManagementProceduresDocumentationComponent', () => {
  let component: ManagementProceduresDocumentationComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, SharedModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    component = TestBed.createComponent(ManagementProceduresDocumentationComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Record keeping and documentation')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText(
        'Describe the organisation’s document retention process, especially for data and information gathered about emissions under the UK ETS for at least 10 years. Include how data is stored so that information can be made available when requested by the regulator or verifier.',
      ),
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        'For more details, see Annex IX of the Monitoring and Reporting Regulations, as defined in Article 4 of the Greenhouse Gas Emissions Trading Scheme Order 2020.',
      ),
    ).toBeInTheDocument();
  });
});
