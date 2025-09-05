import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { alrMockReviewState, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { AlrDeterminationProceedAuthorityGuard } from './determination-proceed-authority.guard';

describe('AlrDeterminationProceedAuthorityGuard', () => {
  let guard: AlrDeterminationProceedAuthorityGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideRouter([])],
    });
    guard = TestBed.inject(AlrDeterminationProceedAuthorityGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  beforeEach(() => {
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.params = { taskId: 1 };
    activatedRouteSnapshot.data = { sectionKey: 'determination' };
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to main task if not proceed authority determination', async () => {
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
          },
        },
        regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary page if wizard is populated', async () => {
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
        regulatorReviewSectionsCompleted: { ALC: true, DETERMINATION: false },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/review/determination/proceed-authority/summary`));
  });
});
