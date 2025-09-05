import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlSegment,
  UrlTree,
} from '@angular/router';

import { firstValueFrom, Observable } from 'rxjs';

import {
  alrMockAuthorityPayload,
  mockAlrAuthorityCompletedPayload,
  mockAlrAuthorityStateBuild,
} from '@tasks/alr/test/mock-authority';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AlrReponseSummaryGuard } from './response-summary.guard';

describe('AlrReponseSummaryGuard', () => {
  let guard: AlrReponseSummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideRouter([])],
    });
    guard = TestBed.inject(AlrReponseSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  describe('For authority response', () => {
    beforeEach(() => {
      activatedRouteSnapshot = new ActivatedRouteSnapshot();
      activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
      activatedRouteSnapshot.params = { taskId: 1 };
    });

    const routerStateSnapshot = {
      url: '/tasks/1/alr/authority/response/summary',
    } as RouterStateSnapshot;

    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should return true if section completed', async () => {
      store.setState(
        mockAlrAuthorityStateBuild({
          ...(mockAlrAuthorityCompletedPayload as any),
          authorityReviewSectionsCompleted: { authorityResponse: true },
        }),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    });

    it('should redirect to base url if wizard not completed', async () => {
      store.setState(
        mockAlrAuthorityStateBuild({
          ...(alrMockAuthorityPayload as any),
        }),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/tasks/1/alr/authority/response`));
    });
  });
});
