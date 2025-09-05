import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { alrMockReviewState, mockAlrReviewStateBuild } from '@tasks/alr/test/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { AlrDeterminationCloseGuard } from './determination-close.guard';

describe('AlrDeterminationCloseGuard', () => {
  let guard: AlrDeterminationCloseGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideRouter([])],
    });
    guard = TestBed.inject(AlrDeterminationCloseGuard);
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

  it('should redirect to main task if not close determination', async () => {
    store.setState(
      mockAlrReviewStateBuild({
        regulatorReviewOutcome: {
          ...(
            alrMockReviewState.requestTaskItem.requestTask
              .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
          )?.regulatorReviewOutcome,
          determination: {
            type: 'CLOSED_ALR',
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
