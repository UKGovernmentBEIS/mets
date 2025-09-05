import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { alrMockReviewState, mockAlrReviewPostBuild, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { AlrPreliminaryAllocationComponent } from './preliminary-allocation.component';

describe('PreliminaryAllocationComponent', () => {
  let component: AlrPreliminaryAllocationComponent;
  let fixture: ComponentFixture<AlrPreliminaryAllocationComponent>;
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub({ taskId: 1 });

  class Page extends BasePage<AlrPreliminaryAllocationComponent> {
    get needsOfficialNotice() {
      return this.queryAll<HTMLInputElement>('input[name$="needsOfficialNotice"]');
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrPreliminaryAllocationComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

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
            reason: 'A comment',
            articleReasonGroupType: 'ARTICLE_6A_REASONS',
            articleReasonItems: ['SETTING_ALLOCATION_UNDER_ARTICLE_3A', 'SETTING_HAL_AND_ALLOCATION_UNDER_ARTICLE_3A'],
            hasWithholdingOfAllowances: true,
            withholdingAllowancesNotice: {
              noticeIssuedDate: new Date('2021-10-10'),
              withholdingOfAllowancesComment: 'A comment',
            },
          },
        },
        regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
        regulatorReviewAttachments: {},
      }),
    );

    fixture = TestBed.createComponent(AlrPreliminaryAllocationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit for new official notice', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryList).toEqual(['Select yes or no']);

    page.needsOfficialNotice[0].click();
    fixture.detectChanges();

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
              hasWithholdingOfAllowances: true,
              withholdingAllowancesNotice: {
                noticeIssuedDate: new Date('2021-10-10'),
                withholdingOfAllowancesComment: 'A comment',
              },
              needsOfficialNotice: true,
            },
          },
        },
        { ALC: true, DETERMINATION: false },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
      relativeTo: activatedRoute,
    });
  });
});
