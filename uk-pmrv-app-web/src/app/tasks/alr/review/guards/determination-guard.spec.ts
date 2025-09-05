import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { alrMockReviewState, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { AlrDeterminationGuard } from './determination-guard';

describe('AlrDeterminationGuard', () => {
  let guard: AlrDeterminationGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideRouter([])],
    });
    guard = TestBed.inject(AlrDeterminationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  beforeEach(() => {
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.params = { taskId: 1 };
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true if can be started and determination not exist yet', async () => {
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {},
        },
        regulatorReviewGroupDecisions: { ALR: { reviewDataType: 'ALR_DATA', type: 'ACCEPTED' } },
        regulatorReviewSectionsCompleted: { ALR: true, OPINION_STATEMENT: true, OVERALL_DECISION: true, ALC: true },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to task if cannot start yet', async () => {
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {},
        },
        regulatorReviewGroupDecisions: { ALR: { reviewDataType: 'ALR_DATA', type: 'ACCEPTED' } },
        regulatorReviewSectionsCompleted: { ALR: false, OPINION_STATEMENT: true, OVERALL_DECISION: true, ALC: true },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/review`));
  });

  it('should redirect to summary determination if proceed authority determination already exist', async () => {
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
              noticeIssuedDate: '2022-08-10',
              withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
            },
            needsOfficialNotice: true,
          },
        },
        regulatorReviewGroupDecisions: { ALR: { reviewDataType: 'ALR_DATA', type: 'ACCEPTED' } },
        regulatorReviewSectionsCompleted: {
          DETERMINATION: false,
          ALR: true,
          OPINION_STATEMENT: true,
          OVERALL_DECISION: true,
          ALC: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/review/determination/proceed-authority/summary`));
  });

  it('should redirect to summary determination if close determination already exist and status is true', async () => {
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {
            type: 'CLOSED_ALR',
            reason: 'A comment',
            alrFile: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5',
            files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
          },
        },
        regulatorReviewGroupDecisions: { ALR: { reviewDataType: 'ALR_DATA', type: 'ACCEPTED' } },
        regulatorReviewSectionsCompleted: {
          DETERMINATION: false,
          ALR: true,
          OPINION_STATEMENT: true,
          OVERALL_DECISION: true,
          ALC: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/review/determination/close/summary`));
  });
});
