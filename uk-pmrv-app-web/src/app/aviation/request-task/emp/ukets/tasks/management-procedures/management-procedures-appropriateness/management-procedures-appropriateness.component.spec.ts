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
import { ManagementProceduresAppropriatenessComponent } from './management-procedures-appropriateness.component';

describe('ManagementProceduresAppropriatenessComponent', () => {
  let component: ManagementProceduresAppropriatenessComponent;

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

    TestBed.inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER).addSmallEmittersForms();

    component = TestBed.createComponent(ManagementProceduresAppropriatenessComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Monitoring plan appropriateness')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(screen.getByText('that the plan is up to date')).toBeInTheDocument();
    expect(screen.getByText('how often the plan is evaluated')).toBeInTheDocument();
    expect(
      screen.getByText('that any changes identified from reviews and visits are communicated to the regulator'),
    ).toBeInTheDocument();
  });
});
