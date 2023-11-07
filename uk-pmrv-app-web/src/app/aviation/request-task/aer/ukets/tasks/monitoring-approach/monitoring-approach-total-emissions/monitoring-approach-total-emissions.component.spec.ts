import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AerMonitoringApproachFormProvider } from '../monitoring-approach-form.provider';
import { MonitoringApproachTotalEmissionsComponent } from './monitoring-approach-total-emissions.component';

describe('MonitoringApproachTotalEmissionsComponent', () => {
  let fixture: ComponentFixture<MonitoringApproachTotalEmissionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AerMonitoringApproachFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MonitoringApproachTotalEmissionsComponent);
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
