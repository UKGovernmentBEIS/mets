import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { ReasonItemGuard } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-item.guard';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('ReasonItemGuard', () => {
  let guard: ReasonItemGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('1', null)];
  activatedRouteSnapshot.params = { taskId: 1, index: 1 };

  const routerStateSnapshot = {
    url: '/tasks/1/aer/verification-submit/overall-decision/reason-list/1',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(ReasonItemGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState(
      mockStateBuild({
        overallAssessment: {
          type: 'VERIFIED_WITH_COMMENTS',
          reasons: ['Reason 1'],
        },
      }),
    );
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to list', async () => {
    store.setState(
      mockStateBuild({
        overallAssessment: {
          type: 'VERIFIED_WITH_COMMENTS',
        },
      }),
    );
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/overall-decision/reason-list'));

    store.setState(
      mockStateBuild({
        overallAssessment: undefined,
      }),
    );
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/overall-decision/reason-list'));
  });
});
