import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { mockState } from '../../testing/mock-state';
import { BDRS2OutcomeCovidAdjustmentsComponent } from './outcome-covid-adjustments.component';

describe('BDRS2OutcomeCovidAdjustmentsComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: BDRS2OutcomeCovidAdjustmentsComponent;
  let fixture: ComponentFixture<BDRS2OutcomeCovidAdjustmentsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'BDRS2',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<BDRS2OutcomeCovidAdjustmentsComponent> {
    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="covidAdjustmentsOpinion"]');
    }
    get notesOperator() {
      return this.getInputValue('#operatorNotes');
    }
    set notesOperator(value: string) {
      this.setInputValue('#operatorNotes', value);
    }
    get notes() {
      return this.getInputValue('#internalNotes');
    }
    set notes(value: string) {
      this.setInputValue('#internalNotes', value);
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
    fixture = TestBed.createComponent(BDRS2OutcomeCovidAdjustmentsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
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
      expect(page.header).toEqual('What is your decision on the COVID adjustments?');
      expect(page.decisionRadioButtons.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a free allocation decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select your opinion on the COVID adjustments']);

      page.decisionRadioButtons[0].click();
      page.notes = 'My notes';
      page.notesOperator = 'My notes';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'BDRS2_REGULATOR_REVIEW_SAVE',
        requestTaskActionPayload: {
          payloadType: 'BDRS2_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: {
            covidAdjustmentsReviewNotes: {
              internalNotes: 'My notes',
              operatorNotes: 'My notes',
            },
            covidAdjustmentsOpinion: 'SENT_TO_AUTHORITY',
          },
          regulatorReviewSectionsCompleted: {
            outcome: false,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('What is your decision on the COVID adjustments?');
    });
  });
});
