import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { MethodBProceduresFormProvider } from '../method-b-procedures-form.provider';
import { MethodBProceduresFuelDensityComponent } from './method-b-procedures-fuel-density.component';

describe('MethodBProceduresFuelDensityComponent', () => {
  let component: MethodBProceduresFuelDensityComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodBProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER).form;

    component = TestBed.createComponent(MethodBProceduresFuelDensityComponent).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Measuring fuel density')).toBeInTheDocument();
  });

  it(`should display correct details`, () => {
    expect(
      screen.getByText(
        'Describe the procedures for determining and recording fuel density values ' +
          '(standard or actual) as used for operational and safety reasons, and provide reference to the relevant internal ' +
          'documentation.',
      ),
    ).toBeInTheDocument();
  });
});
