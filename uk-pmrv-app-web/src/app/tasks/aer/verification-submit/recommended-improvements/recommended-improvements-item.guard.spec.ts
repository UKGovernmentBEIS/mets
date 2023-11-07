import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RecommendedImprovementsItemGuard } from '@tasks/aer/verification-submit/recommended-improvements/recommended-improvements-item.guard';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('RecommendedImprovementsItemGuard', () => {
  let guard: RecommendedImprovementsItemGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(RecommendedImprovementsItemGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        recommendedImprovements: {
          areThereRecommendedImprovements: true,
          recommendedImprovements: [
            {
              reference: 'D1',
              explanation: 'Explanation 1',
            },
          ],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate when `areThereRecommendedImprovements` is true with proper index', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 0 };
    store.setState(
      mockStateBuild({
        recommendedImprovements: {
          areThereRecommendedImprovements: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to `recommended-improvements` when index is greater than array length', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        recommendedImprovements: {
          areThereRecommendedImprovements: true,
          recommendedImprovements: [],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/recommended-improvements'));
  });

  it('should redirect to `recommended-improvements` when `areThereRecommendedImprovements` is false', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        recommendedImprovements: {
          areThereRecommendedImprovements: false,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/recommended-improvements'));
  });
});
