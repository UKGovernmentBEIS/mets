import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivitySummaryTemplateComponent } from '@shared/components/alr';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { ActivityReviewComponent } from './activity-review.component';

describe('ActivityReviewComponent', () => {
  let component: ActivityReviewComponent;
  let fixture: ComponentFixture<ActivityReviewComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'ALR',
    },
  );

  class Page extends BasePage<ActivityReviewComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get decisionHeader() {
      return this.query<HTMLHeadingElement>('fieldset legend').textContent.trim();
    }

    get baselineSummary() {
      return this.query<HTMLDivElement>('app-alr-activity-summary-template');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('li')).map((li) => li.textContent.trim());
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="decision"]');
    }

    get notes() {
      return this.getInputValue('#notes');
    }

    set notes(value: string) {
      this.setInputValue('#notes', value);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryTemplateComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...alrMockReviewState,
      requestTaskItem: {
        ...alrMockReviewState.requestTaskItem,
        requestTask: {
          ...alrMockReviewState.requestTaskItem.requestTask,
          payload: {
            ...alrMockReviewState.requestTaskItem.requestTask.payload,
            regulatorReviewGroupDecisions: {},
            regulatorReviewSectionsCompleted: {},
          } as ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(ActivityReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Review the activity level report and details');
    expect(page.baselineSummary.textContent).toBeTruthy();
  });

  it('should submit a decision and show summary', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    expect(page.decisionHeader).toEqual('What is your decision on the information submitted?');

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Select a decision']);

    page.decisionRadioButtons[0].click();
    page.notes = 'My notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
      requestTaskActionPayload: {
        payloadType: 'ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
        group: 'ALR',
        decision: {
          reviewDataType: 'ALR_DATA',
          type: 'ACCEPTED',
          details: {
            notes: 'My notes',
          },
        },
        regulatorReviewSectionsCompleted: {
          ALR: true,
        },
      },
      requestTaskId: 1,
    });
  });
});
