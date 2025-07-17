import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { mockState } from '@tasks/bdr/review/testing/mock-state';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { Outcome27aQuestionComponent } from './outcome-27a-question.component';
import { QuestionFormProvider } from './outcome-27a-question-form.provider';

describe('Outcome27aQuestionComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;
  let component: Outcome27aQuestionComponent;
  let fixture: ComponentFixture<Outcome27aQuestionComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '1' });
  class Page extends BasePage<Outcome27aQuestionComponent> {
    get decisionCheckbox() {
      return this.queryAll<HTMLInputElement>('.govuk-checkboxes__input');
    }

    get header() {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get errorSummary(): HTMLDivElement {
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
    fixture = TestBed.createComponent(Outcome27aQuestionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, Outcome27aQuestionComponent],
      providers: [
        QuestionFormProvider,
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
              regulatorReviewOutcome: {
                hasOperatorMetDataSubmissionRequirements: null,
              },
            } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
          },
        },
      });
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display an empty form', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.header).toEqual('Has the operator met the 27A data submission requirements?');
      expect(page.decisionCheckbox.every((checkbox) => !checkbox.checked)).toBeTruthy();
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Select if the operator has met the data submission requirements and this information has been sent to the UK ETS authority',
      ]);
      expect(page.errorSummaryList.length).toEqual(1);
    });

    it('should submit a 27a data submission requirement', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual([
        'Select if the operator has met the data submission requirements and this information has been sent to the UK ETS authority',
      ]);

      page.decisionCheckbox[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
        requestTaskActionType: 'BDR_REGULATOR_REVIEW_SAVE',
        requestTaskActionPayload: {
          payloadType: 'BDR_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: {
            hasOperatorMetDataSubmissionRequirements: true,
          },
          regulatorReviewSectionsCompleted: {
            outcome: false,
          },
        },
        requestTaskId: 1,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../upload-files'], { relativeTo: activatedRoute });
    });
  });
});
