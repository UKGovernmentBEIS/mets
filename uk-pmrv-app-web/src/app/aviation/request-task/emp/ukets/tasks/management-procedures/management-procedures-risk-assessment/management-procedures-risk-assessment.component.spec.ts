import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { FileInputComponent } from '@shared/file-input/file-input.component';
import { SharedModule } from '@shared/shared.module';
import { asyncData, mockClass } from '@testing';
import { screen } from '@testing-library/dom';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresRiskAssessmentComponent } from './management-procedures-risk-assessment.component';

describe('ManagementProceduresRiskAssessmentComponent', () => {
  let component: ManagementProceduresRiskAssessmentComponent;
  let fixture: ComponentFixture<ManagementProceduresRiskAssessmentComponent>;

  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

  beforeEach(async () => {
    attachmentService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { uuid: '60fe9548-ac65-492a-b057-60033b0fbbed' } })),
    );

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, RouterTestingModule, SharedModule],
      declarations: [FileInputComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    TestBed.inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER).addRiskAssessmentForm();
    fixture = TestBed.createComponent(ManagementProceduresRiskAssessmentComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct description', async () => {
    expect(
      screen.getByText(
        'Provide the results of a risk assessment that identifies the key risks, their risk rating and control measures related to your monitoring and reporting processes.',
      ),
    ).toBeInTheDocument();
  });
});
