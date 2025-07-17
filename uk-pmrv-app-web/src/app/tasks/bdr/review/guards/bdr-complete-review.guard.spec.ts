import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { mockBDRApplicationSubmitPayload } from '@tasks/bdr/submit/testing/mock-bdr-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockState } from '../testing/mock-state';
import { BdrCompleteReviewGuard } from './bdr-complete-review.guard';

describe('BdrCompleteReviewGuard', () => {
  let store: CommonTasksStore;
  let guard: BdrCompleteReviewGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrService, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });
    guard = TestBed.inject(BdrCompleteReviewGuard);
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
            ...mockBDRApplicationSubmitPayload,
            bdr: {
              isApplicationForFreeAllocation: false,
              statusApplicationType: 'HSE',
              infoIsCorrectChecked: true,
              hasMmp: false,
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
            bdrSectionsCompleted: {
              baseline: true,
            },
            regulatorReviewSectionsCompleted: {
              BDR: true,
              outcome: true,
              OVERALL_DECISION: true,
              OPINION_STATEMENT: true,
            },
            regulatorReviewGroupDecisions: {
              BDR: {
                type: 'ACCEPTED',
                details: {
                  notes: '123',
                },
                reviewDataType: 'BDR_DATA',
              },
              OVERALL_DECISION: {
                type: 'ACCEPTED',
                details: {
                  notes: '123',
                },
                reviewDataType: 'VERIFICATION_REPORT_DATA',
              },
              OPINION_STATEMENT: {
                type: 'ACCEPTED',
                details: {
                  notes: '123',
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
            ...mockBDRApplicationSubmitPayload,
            bdr: {
              isApplicationForFreeAllocation: false,
              statusApplicationType: 'HSE',
              infoIsCorrectChecked: true,
              hasMmp: false,
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
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
    ).resolves.toEqual(router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdr/review`));
  });
});
