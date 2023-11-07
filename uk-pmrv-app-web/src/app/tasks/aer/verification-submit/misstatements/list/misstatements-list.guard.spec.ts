import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { MisstatementsListGuard } from '@tasks/aer/verification-submit/misstatements/list/misstatements-list.guard';
import { mockStateBuild } from '@tasks/aer/verification-submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('MisstatementsListGuard', () => {
  let guard: MisstatementsListGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });

    guard = TestBed.inject(MisstatementsListGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.params = { taskId: 1 };
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
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

  it('should redirect to `misstatements` when `uncorrectedMisstatements` is empty or null', async () => {
    store.setState(
      mockStateBuild({
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/misstatements'));
  });
});
