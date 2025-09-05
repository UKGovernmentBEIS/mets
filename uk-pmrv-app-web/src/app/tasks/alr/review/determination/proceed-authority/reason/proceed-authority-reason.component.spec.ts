import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrProceedAuthorityReasonComponent } from './proceed-authority-reason.component';

describe('AlrProceedAuthorityReasonComponent', () => {
  let component: AlrProceedAuthorityReasonComponent;
  let fixture: ComponentFixture<AlrProceedAuthorityReasonComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({
    taskId: 1,
  });

  class Page extends BasePage<AlrProceedAuthorityReasonComponent> {
    get articleReasonGroupTypeRadios(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="articleReasonGroupType"]');
    }

    get article6aReasonsCheckboxes(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="article6aReasons"]');
    }

    get article34HReasonItemsCheckboxes(): HTMLInputElement[] {
      return this.queryAll<HTMLInputElement>('input[name$="article34HReasonItems"]');
    }

    get reasonTextArea() {
      return this.query<HTMLInputElement>('textarea[name$="reason"]');
    }
    get reason(): string {
      return this.getInputValue('#reason');
    }
    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AlrProceedAuthorityReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AlrTaskSharedModule],
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        DestroySubject,
      ],
    }).compileComponents();
  });

  describe('for new reason', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockAlrReviewStateBuild({
          regulatorReviewOutcome: {
            ...(
              alrMockReviewState.requestTaskItem.requestTask
                .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewOutcome,
            determination: {
              type: 'PROCEED_TO_AUTHORITY',
            },
          },
          regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Select an option', 'Enter a comment']);

      page.articleReasonGroupTypeRadios[0].click();
      fixture.detectChanges();

      page.article6aReasonsCheckboxes[1].click();
      page.article6aReasonsCheckboxes[2].click();

      page.reason = 'A comment';

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockAlrReviewPostBuild(
          {
            regulatorReviewOutcome: {
              ...(
                alrMockReviewState.requestTaskItem.requestTask
                  .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
              )?.regulatorReviewOutcome,
              determination: {
                type: 'PROCEED_TO_AUTHORITY',
                reason: 'A comment',
                articleReasonGroupType: 'ARTICLE_6A_REASONS',
                articleReasonItems: [
                  'SETTING_ALLOCATION_UNDER_ARTICLE_3A',
                  'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A',
                ],
              },
            },
          },
          {
            ALC: true,
            DETERMINATION: false,
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../', 'withholding-of-allowances'], {
        relativeTo: activatedRoute,
      });
    });
  });
});
