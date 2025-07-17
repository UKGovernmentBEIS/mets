import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../../store';

describe('EmpVariationRegulatorLedDecisionGroupComponent', () => {
  let component: EmpVariationRegulatorLedDecisionGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);

  @Component({
    template: `
      <app-emp-variation-regulator-led-decision-group
        taskKey="abbreviations"></app-emp-variation-regulator-led-decision-group>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, EmpVariationRegulatorLedDecisionGroupComponent],
      providers: [EmpVariationRegulatorLedDecisionGroupFormProvider, { provide: TasksService, useValue: tasksService }],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD',
            emissionsMonitoringPlan: {
              abbreviations: {
                exist: false,
              },
            },
            empSectionsCompleted: {
              abbreviations: [false],
            },
          },
        },
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: true,
      tasksState: {
        abbreviations: { status: 'in progress' },
      },
    } as any);

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(
      By.directive(EmpVariationRegulatorLedDecisionGroupComponent),
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the decision', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="button"]').click();
    fixture.detectChanges();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();

    component.form.setValue({
      notes: 'My notes',
      variationScheduleItems: [{ item: 'My item' }],
    });
    component.onSubmit();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED',
      requestTaskActionPayload: {
        group: 'ABBREVIATIONS_AND_DEFINITIONS',
        decision: {
          notes: 'My notes',
          variationScheduleItems: ['My item'],
        },
        empSectionsCompleted: {
          abbreviations: [true],
        },
        payloadType: 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
      },
    });
  });
});
