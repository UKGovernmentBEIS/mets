import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { MethodAProceduresFormProvider } from '../method-a-procedures-form.provider';
import { MethodAProceduresFuelDensityComponent } from './method-a-procedures-fuel-density.component';

describe('MethodAProceduresFuelDensityComponent', () => {
  let component: MethodAProceduresFuelDensityComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MethodAProceduresFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    TestBed.inject<MethodAProceduresFormProvider>(TASK_FORM_PROVIDER).form;

    component = TestBed.createComponent(MethodAProceduresFuelDensityComponent).componentInstance;
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
