import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockReview, mockState } from '@tasks/aer/review/testing/mock-review';
import { AerReviewGroupDecisionComponent } from '@tasks/aer/shared/components/decision/aer-review-group-decision/aer-review-group-decision.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AerApplicationReviewRequestTaskPayload, TasksService } from 'pmrv-api';

describe('AerReviewGroupDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: AerReviewGroupDecisionComponent;
  let fixture: ComponentFixture<AerReviewGroupDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'INSTALLATION_DETAILS',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AerReviewGroupDecisionComponent> {
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
    fixture = TestBed.createComponent(AerReviewGroupDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
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
              reviewGroupDecisions: {},
              reviewSectionsCompleted: {},
            } as AerApplicationReviewRequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.header).toEqual('What is your decision on the information submitted?');
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
      expect(page.errorSummaryList).toEqual(['Select a decision for this review group']);

      page.decisionRadioButtons[0].click();
      page.notes = 'My notes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AER_SAVE_REVIEW_GROUP_DECISION',
        requestTaskActionPayload: {
          payloadType: 'AER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'INSTALLATION_DETAILS',
          decision: {
            reviewDataType: 'AER_DATA',
            type: 'ACCEPTED',
            details: {
              notes: 'My notes',
            },
          },
          reviewSectionsCompleted: {
            INSTALLATION_DETAILS: true,
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

  describe('for existing decision', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show summary', () => {
      expect(page.header).toEqual('Decision Summary  Change');
      expect(page.summaryListValues).toEqual([
        ['Decision status', 'Accepted'],
        ['Notes', 'Notes'],
      ]);
    });

    it('should edit and submit new decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.changeLink.click();
      fixture.detectChanges();

      page.decisionRadioButtons[1].click();
      page.requiredChangesValue = 'Changes required';
      page.notes = 'My notes 2';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'AER_SAVE_REVIEW_GROUP_DECISION',
        requestTaskActionPayload: {
          payloadType: 'AER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'INSTALLATION_DETAILS',
          decision: {
            reviewDataType: 'AER_DATA',
            type: 'OPERATOR_AMENDS_NEEDED',
            details: {
              notes: 'My notes 2',
              requiredChanges: [
                {
                  files: [],
                  reason: 'Changes required',
                },
              ],
            },
          },
          reviewSectionsCompleted: mockReview.reviewSectionsCompleted,
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('Decision Summary  Change');
      expect(page.summaryListValues).toEqual([
        ['Decision status', 'Operator amends needed'],
        ['Changes required by operator', '1. Changes required'],
        ['Notes', 'My notes 2'],
      ]);
    });
  });
});
