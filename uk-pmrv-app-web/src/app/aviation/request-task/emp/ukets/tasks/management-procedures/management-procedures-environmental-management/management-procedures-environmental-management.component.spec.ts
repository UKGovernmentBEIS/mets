import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ManagementProceduresFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresEnvironmentalManagementComponent } from './management-procedures-environmental-management.component';

describe('ManagementProceduresEnvironmentalManagementComponent', () => {
  let component: ManagementProceduresEnvironmentalManagementComponent;
  let fixture: ComponentFixture<ManagementProceduresEnvironmentalManagementComponent>;

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
    fixture = TestBed.createComponent(ManagementProceduresEnvironmentalManagementComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
