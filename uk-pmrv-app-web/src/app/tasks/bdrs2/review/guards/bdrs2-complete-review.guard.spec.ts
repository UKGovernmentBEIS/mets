import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { mockBDRS2ApplicationSubmitPayload } from '@tasks/bdrs2/submit/testing/mock-bdrs2-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockState } from '../testing/mock-state';
import { BdrS2CompleteReviewGuard } from './bdrs2-complete-review.guard';

describe('BdrS2CompleteReviewGuard', () => {
  let store: CommonTasksStore;
  let guard: BdrS2CompleteReviewGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrS2Service, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });
    guard = TestBed.inject(BdrS2CompleteReviewGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate true', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockBDRS2ApplicationSubmitPayload,
            bdrs2: {
              mmpFiles: {
                file: '9446912f-8d2c-469c-8fae-0c5cb7a652f1',
              },
              bdrs2Files: {
                file: 'bb57ccc2-3af5-48f7-beb5-7f0ca9ef9655',
                supportingFiles: ['d9bffd8b-c78e-40c6-a062-c0caa26ccae5'],
              },
              bdrs2guardQuestions: {
                inEiteSector: true,
                covidAdjustments: false,
                applicationWithdrawalReason: 'vlakies moreeeee',
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                requiresAdditionalSubInstallationSplitsForCbam: false,
              },
            },
            bdrSectionsCompleted: {
              baseline: true,
            },
            regulatorReviewSectionsCompleted: {
              BDRS2: true,
              outcome: true,
              OVERALL_DECISION: true,
              OPINION_STATEMENT: true,
            },
            regulatorReviewGroupDecisions: {
              BDRS2: {
                type: 'ACCEPTED',
                details: {
                  notes: 'notes',
                },
                reviewDataType: 'BDRS2_DATA',
              },
              OVERALL_DECISION: {
                type: 'ACCEPTED',
                details: {
                  notes: 'notes',
                },
                reviewDataType: 'VERIFICATION_REPORT_DATA',
              },
              OPINION_STATEMENT: {
                type: 'ACCEPTED',
                details: {
                  notes: 'notes',
                },
                reviewDataType: 'VERIFICATION_REPORT_DATA',
              },
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not enter complete ', async () => {
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: {
          ...mockState.requestTaskItem.requestTask,
          payload: {
            ...mockBDRS2ApplicationSubmitPayload,
            bdr: {
              mmpFiles: {
                file: '9446912f-8d2c-469c-8fae-0c5cb7a652f1',
              },
              bdrs2Files: {
                file: 'bb57ccc2-3af5-48f7-beb5-7f0ca9ef9655',
                supportingFiles: ['d9bffd8b-c78e-40c6-a062-c0caa26ccae5'],
              },
              bdrs2guardQuestions: {
                inEiteSector: true,
                covidAdjustments: false,
                applicationWithdrawalReason: 'vlakies moreeeee',
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                requiresAdditionalSubInstallationSplitsForCbam: false,
              },
            },
            bdrSectionsCompleted: {
              baseline: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdrs2/review`));
  });
});
