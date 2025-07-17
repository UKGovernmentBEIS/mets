import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { AER_CORSIA_ANNUAL_OFFSETTING, annualOffsettingMockBuild } from '../../mocks/mock-annual-offsetting';
import { AnnualOffsettingRequirementNotifyOperatorComponent } from './notify-operator.component';

describe('NotifyOperatorComponent', () => {
  let component: AnnualOffsettingRequirementNotifyOperatorComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnualOffsettingRequirementNotifyOperatorComponent, RouterTestingModule],
      providers: [
        DestroySubject,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      annualOffsettingMockBuild({
        aviationAerCorsiaAnnualOffsetting: AER_CORSIA_ANNUAL_OFFSETTING,
        aviationAerCorsiaAnnualOffsettingSectionsCompleted: { offsettingRequirements: true },
      }) as any,
    );

    fixture = TestBed.createComponent(AnnualOffsettingRequirementNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
