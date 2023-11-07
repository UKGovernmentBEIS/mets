import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';
import { screen } from '@testing-library/dom';

import { TasksService } from 'pmrv-api';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresDocumentationRecordComponent } from './management-procedures-documentation-record.component';

describe('ManagementProceduresDocumentationRecordComponent', () => {
  let component: ManagementProceduresDocumentationRecordComponent;
  let fixture: ComponentFixture<ManagementProceduresDocumentationRecordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, SharedModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ManagementProceduresDocumentationRecordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Documentation and record keeping plan')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText(
        'Identify the data stored, the IT systems used and list any applied data management and IT standards, where relevant.',
      ),
    ).toBeInTheDocument();
  });
});
