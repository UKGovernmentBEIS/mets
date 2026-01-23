import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockStateBuild } from '../testing/mock-state';
import { BDRS2BaselineStepGuard } from './baseline-step-guard';

describe('BDRS2BaselineStepGuard', () => {
  let guard: BDRS2BaselineStepGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrS2Service, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });

    guard = TestBed.inject(BDRS2BaselineStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if data model is wrong', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    store.setState(
      mockStateBuild({
        bdrs2: {
          bdrs2guardQuestions: {
            applicationWithdrawalReason: undefined,
            continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
          },
        },
        bdrs2SectionsCompleted: {
          baseline: false,
        },
      }),
    );

    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should go to summary  if  data model is complete', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    store.setState({
      ...mockStateBuild({
        bdrs2: {
          bdrs2guardQuestions: {
            applicationWithdrawalReason: undefined,
            continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
            covidAdjustments: true,
            inEiteSector: true,
            requiresAdditionalSubInstallationSplitsForCbam: true,
          },
        },
        bdrs2SectionsCompleted: {
          baseline: false,
        },
      }),
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot))).resolves.toEqual(
      router.parseUrl('/tasks/1/bdrs2/submit/baseline/summary'),
    );
  });
});
