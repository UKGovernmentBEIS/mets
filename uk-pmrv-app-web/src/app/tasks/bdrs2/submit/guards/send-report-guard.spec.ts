import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockBDRS2ApplicationSubmitPayload, mockBdrS2State } from '../testing/mock-bdrs2-payload';
import { Bdrs2SendReportGuard } from './send-report-guard';

describe('Bdrs2SendReportGuard', () => {
  let store: CommonTasksStore;
  let guard: Bdrs2SendReportGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrS2Service, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });
    guard = TestBed.inject(Bdrs2SendReportGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState({
      ...mockBdrS2State,
      requestTaskItem: {
        ...mockBdrS2State.requestTaskItem,
        requestTask: {
          ...mockBdrS2State.requestTaskItem.requestTask,
          payload: {
            ...mockBDRS2ApplicationSubmitPayload,
            bdrs2: {
              bdrs2guardQuestions: {
                applicationWithdrawalReason: undefined,
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                covidAdjustments: true,
                inEiteSector: true,
                requiresAdditionalSubInstallationSplitsForCbam: false,
              },
              bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
              mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
            },
            bdrs2SectionsCompleted: {
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
      ...mockBdrS2State,
      requestTaskItem: {
        ...mockBdrS2State.requestTaskItem,
        requestTask: {
          ...mockBdrS2State.requestTaskItem.requestTask,
          payload: {
            ...mockBDRS2ApplicationSubmitPayload,
            bdrs2: {
              bdrs2guardQuestions: {
                applicationWithdrawalReason: undefined,
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                covidAdjustments: true,
                inEiteSector: true,
                requiresAdditionalSubInstallationSplitsForCbam: true,
              },
              bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
              mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
            },
            verificationPerformed: true,
            bdrs2SectionsCompleted: {
              baseline: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdrs2/submit/send-report/regulator`),
    );
  });

  it('should navigate to verification if free allocation, sector and cbam are true and verification not performed', async () => {
    store.setState({
      ...mockBdrS2State,
      requestTaskItem: {
        ...mockBdrS2State.requestTaskItem,
        requestTask: {
          ...mockBdrS2State.requestTaskItem.requestTask,
          payload: {
            ...mockBDRS2ApplicationSubmitPayload,
            bdrs2: {
              bdrs2guardQuestions: {
                applicationWithdrawalReason: undefined,
                continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
                covidAdjustments: true,
                inEiteSector: true,
                requiresAdditionalSubInstallationSplitsForCbam: true,
              },
              bdrs2Files: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
              mmpFiles: { file: 'ebff80af-8c13-4f5a-b1eb-75b74a2121c5' },
            },
            verificationPerformed: false,
            bdrs2SectionsCompleted: {
              baseline: true,
            },
          },
        },
      },
    } as CommonTasksState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`tasks/${activatedRouteSnapshot.params.taskId}/bdrs2/submit/send-report/verifier`),
    );
  });
});
