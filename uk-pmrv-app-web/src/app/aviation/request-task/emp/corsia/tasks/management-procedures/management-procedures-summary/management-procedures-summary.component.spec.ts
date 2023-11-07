import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';

import { ManagementProceduresCorsiaFormProvider } from '../management-procedures-form.provider';
import { ManagementProceduresSummaryComponent } from './management-procedures-summary.component';

describe('ManagementProceduresSummaryComponent', () => {
  let component: ManagementProceduresSummaryComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, SharedModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresCorsiaFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    component = TestBed.createComponent(ManagementProceduresSummaryComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
