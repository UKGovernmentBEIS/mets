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
import { ManagementProceduresOutsourcedActivitiesComponent } from './management-procedures-outsourced-activities.component';

describe('ManagementProceduresOutsourcedActivitiesComponent', () => {
  let component: ManagementProceduresOutsourcedActivitiesComponent;

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

    component = TestBed.createComponent(ManagementProceduresOutsourcedActivitiesComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Control of outsourced activities')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText(
        'Describe the procedures used to control outsourced activities. Include what checks are made on the quality of the resulting data.',
      ),
    ).toBeInTheDocument();
  });
});
