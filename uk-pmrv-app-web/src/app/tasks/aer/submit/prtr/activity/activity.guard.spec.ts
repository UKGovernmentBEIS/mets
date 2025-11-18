import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlSegment, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { ActivityGuard } from '@tasks/aer/submit/prtr/activity/activity.guard';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

describe('ActivityGuard', () => {
  let router: Router;
  let guard: ActivityGuard;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [];
  activatedRouteSnapshot.params = { taskId: 276, index: 2 };

  const activatedRouteSnapshotDelete = new ActivatedRouteSnapshot();
  activatedRouteSnapshotDelete.url = [new UrlSegment('delete', null)];
  activatedRouteSnapshotDelete.params = { taskId: 276, index: 4 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient()],
    });
    guard = TestBed.inject(ActivityGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    store.setState(
      mockStateBuild({
        prtrCodes: {
          exist: true,
          codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES', '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not allow', async () => {
    store.setState(
      mockStateBuild({
        prtrCodes: undefined,
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    store.setState(
      mockStateBuild({
        prtrCodes: {
          exist: true,
          codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    store.setState({
      ...mockStateBuild({
        prtrCodes: {
          exist: true,
          codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES', '_2_C_1_HOT_ROLLING_MILLS'],
        },
      }),
      isEditable: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotDelete) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));
  });
});
