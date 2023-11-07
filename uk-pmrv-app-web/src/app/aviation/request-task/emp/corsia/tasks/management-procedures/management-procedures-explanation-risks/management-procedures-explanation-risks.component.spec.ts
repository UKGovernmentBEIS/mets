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
import { ManagementProceduresExplanationRisksComponent } from './management-procedures-explanation-risks.component';

describe('ManagementProceduresExplanationRisksComponent', () => {
  let component: ManagementProceduresExplanationRisksComponent;
  let fixture: ComponentFixture<ManagementProceduresExplanationRisksComponent>;

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

    fixture = TestBed.createComponent(ManagementProceduresExplanationRisksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Explanation of risks')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(screen.getByText('Data management systems and controls are critical for:')).toBeInTheDocument();
    expect(screen.getByText('ensuring data completeness, security and quality')).toBeInTheDocument();
    expect(
      screen.getByText('minimising the risk of a material error or misstatement in the emissions report.'),
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        'List the risks associated with the data management system and the corresponding internal or external control activities for addressing each risk.',
      ),
    ).toBeInTheDocument();
  });
});
