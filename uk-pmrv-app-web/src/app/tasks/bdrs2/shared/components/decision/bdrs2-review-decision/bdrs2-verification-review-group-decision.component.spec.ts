import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/bdrs2/review/testing/mock-state';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { BdrS2VerificationReviewGroupDecisionComponent } from '@tasks/bdrs2/shared/components/decision/bdrs2-review-decision/bdrs2-verification-review-group-decision.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

describe('BdrS2VerificationReviewGroupDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: BdrS2VerificationReviewGroupDecisionComponent;
  let fixture: ComponentFixture<BdrS2VerificationReviewGroupDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'BDRS2',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BdrS2VerificationReviewGroupDecisionComponent> {
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
    fixture = TestBed.createComponent(BdrS2VerificationReviewGroupDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, BdrS2TaskSharedModule, BdrS2VerificationReviewGroupDecisionComponent],
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  describe('for new decision', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {},
              regulatorReviewSectionsCompleted: {},
            } as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.legend).toEqual('What is your decision for the information submitted?');
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
        requestTaskActionType: 'BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
        requestTaskActionPayload: {
          payloadType: 'BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'BDRS2',
          decision: {
            reviewDataType: 'VERIFICATION_REPORT_DATA',
            type: 'ACCEPTED',
            details: {
              notes: 'My notes',
            },
          },
          regulatorReviewSectionsCompleted: {
            BDRS2: true,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header.replace(/\s+/g, ' ')).toEqual('Decision Summary Change');
      expect(page.summaryListValues).toEqual([
        ['Decision status', 'Accepted'],
        ['Notes', 'My notes'],
      ]);
    });
  });
});
