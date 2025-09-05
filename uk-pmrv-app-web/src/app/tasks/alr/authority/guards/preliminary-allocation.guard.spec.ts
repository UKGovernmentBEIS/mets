import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, provideRouter, Router, UrlTree } from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import { mockAlrAuthorityCompletedPayload, mockAlrAuthorityStateBuild } from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AlrPreliminaryAllocationGuard } from './preliminary-allocation.guard';

describe('AlrPreliminaryAllocationGuard', () => {
  let guard: AlrPreliminaryAllocationGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };
  activatedRouteSnapshot.data = { sectionKey: 'authorityResponse' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideRouter([])],
    });
    guard = TestBed.inject(AlrPreliminaryAllocationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should true if index number is valid', async () => {
    store.setState(mockAlrAuthorityStateBuild(mockAlrAuthorityCompletedPayload as any));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary if section completed', async () => {
    store.setState(
      mockAlrAuthorityStateBuild({
        ...(mockAlrAuthorityCompletedPayload as any),
        authorityReviewSectionsCompleted: { authorityResponse: true },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/authority/response/summary`));
  });
});
