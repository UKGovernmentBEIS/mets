import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { noComment, noDate, noSelection } from '@tasks/vir/errors/validation-errors';
import { mockPostBuild, mockStateBuild } from '@tasks/vir/review/testing/mock-state';
import {
  mockStateReview,
  mockVirApplicationReviewPayload,
} from '@tasks/vir/review/testing/mock-vir-application-review-payload';
import { VirTaskReviewModule } from '@tasks/vir/review/vir-task-review.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService, UncorrectedItem } from 'pmrv-api';

import { RecommendationResponseReviewComponent } from './recommendation-response-review.component';

describe('RecommendationResponseReviewComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: RecommendationResponseReviewComponent;
  let fixture: ComponentFixture<RecommendationResponseReviewComponent>;

  const currentItem: UncorrectedItem = mockVirApplicationReviewPayload.verificationData.uncorrectedNonConformities.B1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockStateReview.requestTaskItem.requestTask.id, id: currentItem.reference },
    null,
    {
      verificationDataItem: currentItem,
    },
  );
  const tasksService = mockClass(TasksService);
  const expectedDate = new Date('2023-12-01');
  const expectedNextRoute = '../../B1/summary';

  class Page extends BasePage<RecommendationResponseReviewComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }

    get improvementRequiredButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="improvementRequired"]');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get improvementComments() {
      return this.getInputValue('#improvementComments');
    }

    set improvementComments(value: string) {
      this.setInputValue('#improvementComments', value);
    }

    get operatorActions() {
      return this.getInputValue('#operatorActions');
    }

    set operatorActions(value: string) {
      this.setInputValue('#operatorActions', value);
    }

    set improvementDeadline(date: Date) {
      this.setInputValue(`#improvementDeadline-day`, date.getDate());
      this.setInputValue(`#improvementDeadline-month`, date.getMonth() + 1);
      this.setInputValue(`#improvementDeadline-year`, date.getFullYear());
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RecommendationResponseReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirTaskReviewModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  describe('for new recommendation response review details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild({
          regulatorReviewResponse: {
            regulatorImprovementResponses: {},
            reportSummary: null,
          },
        }),
      );
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.improvementComments).toEqual('');
      expect(page.operatorActions).toEqual('');
      expect(page.submitButton).toBeTruthy();
    });

    it('should display error on empty form submit', () => {
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noSelection, noComment]);
      expect(page.errorSummaryListContents.length).toEqual(2);
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.improvementRequiredButtons[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryListContents).toEqual([noDate, noComment]);

      page.improvementRequiredButtons[0].click();
      page.improvementDeadline = expectedDate;
      page.improvementComments = 'Test improvement comments B1';
      page.operatorActions = 'Test operator actions B1';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatorReviewResponse: {
              regulatorImprovementResponses: {
                B1: {
                  improvementRequired: true,
                  improvementDeadline: expectedDate,
                  improvementComments: 'Test improvement comments B1',
                  operatorActions: 'Test operator actions B1',
                },
              },
              reportSummary: null,
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });

  describe('for existing recommendation response review details', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateReview);
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display all HTMLElements and form with 0 errors', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.heading1).toBeTruthy();
      expect(page.heading1.textContent.trim()).toEqual('Respond to B1');
      expect(page.verificationItem).toBeTruthy();
      expect(page.operatorResponseItem).toBeTruthy();
      expect(page.improvementRequiredButtons).toHaveLength(2);
      expect(page.improvementComments).toEqual('Test improvement comments B1');
      expect(page.operatorActions).toEqual('Test operator actions B1');
      expect(page.submitButton).toBeTruthy();
    });

    it('should submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });

    it('should edit, submit a valid form and navigate to `summary` page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.improvementRequiredButtons[1].click();
      page.improvementComments = 'Test description B1, changed';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            regulatorReviewResponse: {
              ...mockVirApplicationReviewPayload.regulatorReviewResponse,
              regulatorImprovementResponses: {
                ...mockVirApplicationReviewPayload.regulatorReviewResponse.regulatorImprovementResponses,
                [currentItem.reference]: {
                  improvementRequired: false,
                  improvementDeadline: null,
                  improvementComments: 'Test description B1, changed',
                  operatorActions: 'Test operator actions B1',
                },
              },
            },
          },
          { B1: false },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith([expectedNextRoute], { relativeTo: activatedRoute });
    });
  });
});
