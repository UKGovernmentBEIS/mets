import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrVerificationReviewGroupDecisionComponent } from './alr-verification-review-group-decision.component';

describe('AlrVerificationReviewGroupDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: AlrVerificationReviewGroupDecisionComponent;
  let fixture: ComponentFixture<AlrVerificationReviewGroupDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'OPINION_STATEMENT',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AlrVerificationReviewGroupDecisionComponent> {
    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="decision"]');
    }
    get notes() {
      return this.getInputValue('#notes');
    }
    set notes(value: string) {
      this.setInputValue('#notes', value);
    }
    get requiredChangesValue() {
      return this.getInputValue('#requiredChanges.0.reason');
    }
    set requiredChangesValue(value: string) {
      this.setInputValue('#requiredChanges.0.reason', value);
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('a');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get legend() {
      return this.query<HTMLHeadingElement>('.govuk-fieldset__legend').textContent.trim();
    }

    get header() {
      return this.query<HTMLHeadingElement>('h2').textContent.trim();
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('li')).map((li) => li.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrVerificationReviewGroupDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AlrTaskSharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new decision', () => {
    beforeEach(() => {
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
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.legend).toEqual('What is your decision on the information submitted?');
      expect(page.summaryListValues).toEqual([]);
      expect(page.decisionRadioButtons.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a decision and show summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select your decision']);

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
          group: 'OPINION_STATEMENT',
          decision: {
            reviewDataType: 'VERIFICATION_REPORT_DATA',
            type: 'ACCEPTED',
            details: {
              notes: 'My notes',
            },
          },
          regulatorReviewSectionsCompleted: {
            OPINION_STATEMENT: true,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('Decision Summary  Change');
      expect(page.summaryListValues).toEqual([
        ['Decision status', 'Accepted'],
        ['Notes', 'My notes'],
      ]);
    });
  });
});
