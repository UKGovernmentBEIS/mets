import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { DeleteGuard } from './delete.guard';

describe('DeleteGuard', () => {
  let guard: DeleteGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(DeleteGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 0 };
    store.setState(
      mockStateBuild({
        uncorrectedNonCompliances: {
          areThereUncorrectedNonCompliances: true,
          uncorrectedNonCompliances: [
            {
              reference: 'C1',
              explanation: 'Explanation 1',
              materialEffect: false,
            },
          ],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to `list` when index is not found', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        uncorrectedNonCompliances: {
          areThereUncorrectedNonCompliances: true,
          uncorrectedNonCompliances: [
            {
              reference: 'C1',
              explanation: 'Explanation 1',
              materialEffect: false,
            },
          ],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/non-compliances/list'));
  });

  it('should redirect to `list` when `uncorrectedNonCompliances` is empty or null', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        uncorrectedNonCompliances: {
          areThereUncorrectedNonCompliances: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/non-compliances/list'));
  });
});
