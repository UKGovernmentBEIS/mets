import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { SummaryGuard } from '@tasks/aer/verification-submit/summary.guard';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1 };
  activatedRouteSnapshot.data = { aerTask: 'verificationTeamDetails' };

  const routerStateSnapshot = {
    url: '/tasks/1/verification-submit/verifier-details/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState(mockState);
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(mockStateBuild(null, { verificationTeamDetails: [false] }));
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState({
      ...mockState,
      isEditable: false,
    });
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to task', async () => {
    store.setState(
      mockStateBuild(
        {
          verifierContact: null,
          verificationTeamDetails: null,
        },
        { verificationTeamDetails: undefined },
      ),
    );
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/verification-submit/verifier-details'));
  });
});
