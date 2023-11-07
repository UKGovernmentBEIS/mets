import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';
import { screen } from '@testing-library/angular';

import { TasksService } from 'pmrv-api';

import { MonitoringPlanChangesFormProvider } from '../monitoring-plan-changes-form.provider';
import { MonitoringPlanChangesPageComponent } from './monitoring-plan-changes-page.component';

describe('MonitoringPlanChangesPageComponent', () => {
  let fixture: ComponentFixture<MonitoringPlanChangesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: MonitoringPlanChangesFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringPlanChangesPageComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct header`, () => {
    expect(
      screen.getByText('These are the versions of your monitoring plan which were effective during the scheme year.'),
    ).toBeInTheDocument();
  });
});
