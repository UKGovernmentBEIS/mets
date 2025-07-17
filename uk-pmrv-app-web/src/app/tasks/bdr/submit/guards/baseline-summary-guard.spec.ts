import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { mockStateBuild } from '../testing/mock-state';
import { BaselineSummaryGuard } from './baseline-summary-guard';

describe('BaselineSummaryGuard', () => {
  let guard: BaselineSummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1 };
  const routerStateSnapshot = {
    url: '/tasks/1/bdr/submit/baseline/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BdrService, ItemNamePipe, provideHttpClient(withInterceptorsFromDi()), CapitalizeFirstPipe],
    });

    guard = TestBed.inject(BaselineSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if data model is complete', async () => {
    store.setState(
      mockStateBuild({
        bdr: {
          isApplicationForFreeAllocation: false,
          statusApplicationType: 'HSE',
          infoIsCorrectChecked: true,
          files: ['ebff80af-8c13-4f5a-b1eb-75b74a2121c5'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if  data model is wrong', async () => {
    store.setState({
      ...mockStateBuild({
        bdr: {
          statusApplicationType: 'HSE',
        },
        bdrSectionsCompleted: {
          baseline: false,
        },
      }),
      isEditable: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/bdr/submit/baseline'));
  });
});
