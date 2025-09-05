import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { hsetiMockReviewState } from '@tasks/hseti/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { HsetiReviewGroupDecisionComponent } from './hseti-review-group-decision.component';

describe('HsetiReviewGroupDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: HsetiReviewGroupDecisionComponent;
  let fixture: ComponentFixture<HsetiReviewGroupDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'HSETI',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<HsetiReviewGroupDecisionComponent> {
    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="decision"]');
    }
    get capacityIncreaseDescription() {
      return this.getInputValue('#capacityIncreaseDescription');
    }
    set capacityIncreaseDescription(value: string) {
      this.setInputValue('#capacityIncreaseDescription', value);
    }
    get capacityIncreasePermanence() {
      return this.getInputValue('#capacityIncreasePermanence');
    }
    set capacityIncreasePermanence(value: string) {
      this.setInputValue('#capacityIncreasePermanence', value);
    }
    get capacityGreaterThanZeroDescription() {
      return this.getInputValue('#capacityGreaterThanZeroDescription');
    }
    set capacityGreaterThanZeroDescription(value: string) {
      this.setInputValue('#capacityGreaterThanZeroDescription', value);
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
    fixture = TestBed.createComponent(HsetiReviewGroupDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, HseTiTaskSharedModule],
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
        ...hsetiMockReviewState,
        requestTaskItem: {
          ...hsetiMockReviewState.requestTaskItem,
          requestTask: {
            ...hsetiMockReviewState.requestTaskItem.requestTask,
            payload: {
              ...hsetiMockReviewState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {},
              regulatorReviewSectionsCompleted: {},
            } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
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
      expect(page.capacityIncreaseDescription).toEqual('');
      expect(page.capacityIncreasePermanence).toEqual('');
      expect(page.capacityGreaterThanZeroDescription).toEqual('');
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a decision and show summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Enter a comment',
        'Enter a comment',
        'Enter a comment',
        'Select a decision',
      ]);

      page.decisionRadioButtons[0].click();
      page.capacityIncreaseDescription = 'capacityIncreaseDescription';
      page.capacityIncreasePermanence = 'capacityIncreasePermanence';
      page.capacityGreaterThanZeroDescription = 'capacityGreaterThanZeroDescription';
      page.notes = 'My notes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
        requestTaskActionPayload: {
          payloadType: 'HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
          group: 'HSETI',
          decision: {
            type: 'ACCEPTED',
            details: {
              capacityIncreaseDescription: 'capacityIncreaseDescription',
              capacityIncreasePermanence: 'capacityIncreasePermanence',
              capacityGreaterThanZeroDescription: 'capacityGreaterThanZeroDescription',
              notes: 'My notes',
            },
          },
          regulatorReviewSectionsCompleted: {
            HSETI: true,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('Decision Summary  Change decision');
      expect(page.summaryListValues).toEqual([
        [
          'Describe the capacity increase that has been put into operation at the installation',
          'capacityIncreaseDescription',
        ],
        ['Have you confirmed that the capacity increase is permanent?', 'capacityIncreasePermanence'],
        [
          'Describe how the net change in installed capacity at the installation since the reference date is greater than zero',
          'capacityGreaterThanZeroDescription',
        ],
        ['Decision status', 'Accepted'],
        ['Notes', 'My notes'],
      ]);
    });
  });
});
