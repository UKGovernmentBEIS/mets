import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockBDRApplicationSubmitPayload, mockBdrState } from '../testing/mock-bdr-payload';
import { SendReportGuard } from './send-report-guard';

describe('SendReportGuard', () => {
  let store: CommonTasksStore;
  let guard: SendReportGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrService, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });
    guard = TestBed.inject(SendReportGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState({
      ...mockBdrState,
      requestTaskItem: {
        ...mockBdrState.requestTaskItem,
        requestTask: {
          ...mockBdrState.requestTaskItem.requestTask,
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
    ).resolves.toEqual(true);
  });

  it('should navigate to regulator if verification performed', async () => {
    store.setState({
      ...mockBdrState,
      requestTaskItem: {
        ...mockBdrState.requestTaskItem,
        requestTask: {
          ...mockBdrState.requestTaskItem.requestTask,
          payload: {
            ...mockBDRApplicationSubmitPayload,
            bdr: {
              isApplicationForFreeAllocation: false,
              statusApplicationType: 'HSE',
              infoIsCorrectChecked: true,
              hasMmp: false,
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
            verificationPerformed: true,
            bdrSectionsCompleted: {
              baseline: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdr/submit/send-report/regulator`),
    );
  });

  it('should navigate to verification if free allocation true and verification not performed', async () => {
    store.setState({
      ...mockBdrState,
      requestTaskItem: {
        ...mockBdrState.requestTaskItem,
        requestTask: {
          ...mockBdrState.requestTaskItem.requestTask,
          payload: {
            ...mockBDRApplicationSubmitPayload,
            bdr: {
              isApplicationForFreeAllocation: true,
              statusApplicationType: 'HSE',
              infoIsCorrectChecked: true,
              hasMmp: false,
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
            verificationPerformed: false,
            bdrSectionsCompleted: {
              baseline: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdr/submit/send-report/verifier`),
    );
  });

  it('should navigate to verification if verification required is true', async () => {
    store.setState({
      ...mockBdrState,
      requestTaskItem: {
        ...mockBdrState.requestTaskItem,
        requestTask: {
          ...mockBdrState.requestTaskItem.requestTask,
          payload: {
            ...mockBDRApplicationSubmitPayload,
            bdr: {
              isApplicationForFreeAllocation: true,
              statusApplicationType: 'HSE',
              infoIsCorrectChecked: true,
              hasMmp: false,
              files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
            },
            verificationPerformed: false,
            bdrSectionsCompleted: {
              baseline: true,
            },
            regulatorReviewGroupDecisions: {
              BDR: {
                details: {
                  verificationRequired: true,
                },
              },
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdr/submit/send-report/verifier`),
    );
  });
});
