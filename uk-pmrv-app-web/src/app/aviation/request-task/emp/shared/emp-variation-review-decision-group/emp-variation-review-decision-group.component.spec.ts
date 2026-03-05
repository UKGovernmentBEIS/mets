import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../../store';
import { EmpVariationReviewDecisionGroupComponent } from './emp-variation-review-decision-group.component';
import { EmpVariationReviewDecisionGroupFormProvider } from './emp-variation-review-decision-group.provider';

describe('EmpVariationReviewDecisionGroupComponent', () => {
  let component: EmpVariationReviewDecisionGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);

  @Component({
    template: `
      <app-emp-variation-review-decision-group taskKey="abbreviations"></app-emp-variation-review-decision-group>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, EmpVariationReviewDecisionGroupComponent],
      providers: [EmpVariationReviewDecisionGroupFormProvider, { provide: TasksService, useValue: tasksService }],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
          daysRemaining: 6,
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: {
            payloadType: 'EMP_VARIATION_UKETS_APPLICATION_REVIEW_PAYLOAD',
            emissionsMonitoringPlan: {
              abbreviations: {
                exist: true,
              },
            },
            empSectionsCompleted: {
              abbreviations: [true],
            },
            reviewSectionsCompleted: {},
          },
        },
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: true,
      tasksState: {
        abbreviations: { status: 'complete' },
      },
    } as any);

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(EmpVariationReviewDecisionGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the decision', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();

    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeTruthy();

    element.querySelectorAll<HTMLInputElement>('input[type="radio"]')[1].click();
    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION',
      requestTaskActionPayload: {
        group: 'ABBREVIATIONS_AND_DEFINITIONS',
        decision: {
          type: 'REJECTED',
          details: {
            notes: null,
          },
        },
        reviewSectionsCompleted: {
          ABBREVIATIONS_AND_DEFINITIONS: true,
        },
        empSectionsCompleted: {
          abbreviations: [true],
        },
        payloadType: 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      },
    });
  });
});
