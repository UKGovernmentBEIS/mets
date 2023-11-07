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

import { TASK_FORM_PROVIDER } from '../../../../../../request-task/task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresDataFlowComponent } from './management-procedures-data-flow.component';

describe('ManagementProceduresDataFlowComponent', () => {
  let component: ManagementProceduresDataFlowComponent;

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

    component = TestBed.createComponent(ManagementProceduresDataFlowComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct description`, () => {
    expect(
      screen.getByText(
        'Describe the procedures used to manage data flow activities, to ensure data does not contain misstatements and conforms to the approved emissions monitoring plan and Order.',
      ),
    ).toBeInTheDocument();
  });
});
