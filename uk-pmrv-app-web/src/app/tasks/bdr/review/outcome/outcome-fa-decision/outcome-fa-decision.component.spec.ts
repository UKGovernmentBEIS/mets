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

import { OutcomeFaDecisionComponent } from './outcome-fa-decision.component';

describe('OutcomeFaDecisionComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: OutcomeFaDecisionComponent;
  let fixture: ComponentFixture<OutcomeFaDecisionComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'BDR',
    },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OutcomeFaDecisionComponent> {
    get decisionRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="hasRegulatorSentFreeAllocation"]');
    }
    get notesOperator() {
      return this.getInputValue('#freeAllocationNotesOperator');
    }
    set notesOperator(value: string) {
      this.setInputValue('#freeAllocationNotesOperator', value);
    }
    get notes() {
      return this.getInputValue('#freeAllocationNotes');
    }
    set notes(value: string) {
      this.setInputValue('#freeAllocationNotes', value);
    }

    get fa() {
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
    fixture = TestBed.createComponent(OutcomeFaDecisionComponent);
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
      expect(page.header).toEqual('What is your decision for the free allocation?');
      expect(page.decisionRadioButtons.every((radio) => !radio.checked)).toBeTruthy();
      expect(page.notes).toEqual('');
      expect(page.errorSummary).toBeFalsy();
    });

    it('should submit a free allocation decision', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select your opinion on the free allocation application']);

      page.decisionRadioButtons[0].click();
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
            freeAllocationNotes: 'My notes',
            freeAllocationNotesOperator: null,
            hasRegulatorSentFreeAllocation: true,
          },
          regulatorReviewSectionsCompleted: {
            outcome: false,
          },
        },
        requestTaskId: 1,
      });

      expect(page.header).toEqual('What is your decision for the free allocation?');
    });
  });
});
