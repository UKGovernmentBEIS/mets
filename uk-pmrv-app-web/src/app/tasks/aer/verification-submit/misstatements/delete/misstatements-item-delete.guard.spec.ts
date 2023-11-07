import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { MisstatementsItemDeleteGuard } from '@tasks/aer/verification-submit/misstatements/delete/misstatements-item-delete.guard';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('MisttatementsItemDeleteGuard', () => {
  let guard: MisstatementsItemDeleteGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(MisstatementsItemDeleteGuard);
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
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: true,
          uncorrectedMisstatements: [
            {
              reference: 'A1',
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
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: true,
          uncorrectedMisstatements: [
            {
              reference: 'A1',
              explanation: 'Explanation 1',
              materialEffect: false,
            },
          ],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/misstatements/list'));
  });

  it('should redirect to `list` when `uncorrectedMisstatements` is empty or null', async () => {
    activatedRouteSnapshot.params = { taskId: 1, index: 1 };
    store.setState(
      mockStateBuild({
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/misstatements/list'));
  });
});
