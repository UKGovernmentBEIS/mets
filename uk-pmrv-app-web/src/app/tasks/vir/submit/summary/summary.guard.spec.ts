import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { mockStateBuild } from '@tasks/vir/submit/testing/mock-state';
import { mockState } from '@tasks/vir/submit/testing/mock-vir-application-submit-payload';
import { KeycloakService } from 'keycloak-angular';

import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 1, id: 'B1' };

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
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(mockStateBuild(null));
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState({
      ...mockState,
    });
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to recommendation-response', async () => {
    store.setState(
      mockStateBuild({
        operatorImprovementResponses: {
          B1: {
            isAddressed: true,
          },
        },
      }),
    );

    guard.canActivate(activatedRouteSnapshot).subscribe((val) => {
      expect(router.parseUrl).toHaveBeenCalledWith('../recommendation-response');
      expect(val).toBeInstanceOf(UrlTree);
    });
  });
});
