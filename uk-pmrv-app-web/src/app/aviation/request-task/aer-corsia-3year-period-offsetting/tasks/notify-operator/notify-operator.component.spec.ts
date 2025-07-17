import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import {
  AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING,
  threeYearPeriodlOffsettingMockBuild,
} from '../../mocks/mock-3year-period-offsetting';
import { ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent;
  let fixture: ComponentFixture<ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent, RouterTestingModule],
      providers: [
        DestroySubject,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      threeYearPeriodlOffsettingMockBuild({
        aviationAerCorsia3YearPeriodOffsetting: AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING,
        aviationAerCorsia3YearPeriodOffsettingSectionsCompleted: { offsettingRequirements: true },
      }),
    );

    fixture = TestBed.createComponent(ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
