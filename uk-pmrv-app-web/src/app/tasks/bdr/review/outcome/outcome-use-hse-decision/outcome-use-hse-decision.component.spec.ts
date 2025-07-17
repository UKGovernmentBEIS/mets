import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/bdr/review/testing/mock-state';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { OutcomeUseHseDecisionComponent } from './outcome-use-hse-decision.component';

describe('OutcomeUseHseDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: OutcomeUseHseDecisionComponent;
  let fixture: ComponentFixture<OutcomeUseHseDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'BDR',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OutcomeUseHseDecisionComponent> {
    get decisionRadioButtonsHSE() {
      return this.queryAll<HTMLInputElement>('input[name$="hasRegulatorSentHSE"]');
    }
    get decisionRadioButtonsUSE() {
      return this.queryAll<HTMLInputElement>('input[name$="hasRegulatorSentUSE"]');
    }

    get notesOperator() {
      return this.getInputValue('#useHseNotesOperator');
    }
    set notesOperator(value: string) {
      this.setInputValue('#useHseNotesOperator', value);
    }
    get notes() {
      return this.getInputValue('#useHseNotes');
    }
    set notes(value: string) {
      this.setInputValue('#useHseNotes', value);
    }

    get applying() {
      return this.query<HTMLDListElement>('dl');
    }

    get header() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
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
    fixture = TestBed.createComponent(OutcomeUseHseDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, BdrTaskSharedModule, RouterTestingModule],
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
        ...mockState,
        requestTaskItem: {
          ...mockState.requestTaskItem,
          requestTask: {
            ...mockState.requestTaskItem.requestTask,
            payload: {
              ...mockState.requestTaskItem.requestTask.payload,
              regulatorReviewGroupDecisions: {},
              regulatorReviewSectionsCompleted: {},
            } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
          },
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.header).toEqual('What is your decision on the USE/HSE application?');
      expect(page.decisionRadioButtonsHSE.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.decisionRadioButtonsUSE.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a free allocation decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Select your opinion on the hospital and small emitter application',
        'Select your opinion on the ultra small emitter application',
      ]);

      page.decisionRadioButtonsHSE[0].click();
      page.decisionRadioButtonsUSE[0].click();
      page.notes = 'My notes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'BDR_REGULATOR_REVIEW_SAVE',
        requestTaskActionPayload: {
          payloadType: 'BDR_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: {
            hasRegulatorSentHSE: true,
            hasRegulatorSentUSE: true,
            useHseNotes: 'My notes',
            useHseNotesOperator: null,
          },
          regulatorReviewSectionsCompleted: {
            outcome: false,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('What is your decision on the USE/HSE application?');
    });
  });
});
