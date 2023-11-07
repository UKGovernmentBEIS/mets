import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { RequestTaskStore } from '../../../store';
import { AerReviewDecisionGroupComponent } from './aer-review-decision-group.component';
import { AerReviewDecisionGroupFormProvider } from './aer-review-decision-group-form.provider';

describe('AerReviewDecisionGroupComponent', () => {
  let component: AerReviewDecisionGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  let store: RequestTaskStore;

  const tasksService = mockClass(TasksService);

  @Component({
    template: `
      <app-aviation-aer-review-decision-group taskKey="additionalDocuments"></app-aviation-aer-review-decision-group>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, AerReviewDecisionGroupComponent],
      providers: [AerReviewDecisionGroupFormProvider, { provide: TasksService, useValue: tasksService }],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 19,
          type: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
          daysRemaining: 6,
          assigneeFullName: 'TEST_ASSIGNEE',
          payload: {
            payloadType: 'AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD',
            reviewSectionsCompleted: {},
          },
        },
        allowedRequestTaskActions: ['AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION'],
      },
      relatedTasks: [],
      timeline: [],
      isTaskReassigned: false,
      taskReassignedTo: null,
      isEditable: true,
    } as any);

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(AerReviewDecisionGroupComponent)).componentInstance;
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

    element.querySelectorAll<HTMLInputElement>('input[type="radio"]')[0].click();
    element.querySelector<HTMLButtonElement>('button[type="submit"]').click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 19,
      requestTaskActionType: 'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION',
      requestTaskActionPayload: {
        group: 'ADDITIONAL_DOCUMENTS',
        decision: {
          type: 'ACCEPTED',
          reviewDataType: 'AER_DATA',
          details: {
            notes: null,
          },
        },
        reviewSectionsCompleted: {
          ADDITIONAL_DOCUMENTS: true,
        },
        payloadType: 'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      },
    });
  });
});
