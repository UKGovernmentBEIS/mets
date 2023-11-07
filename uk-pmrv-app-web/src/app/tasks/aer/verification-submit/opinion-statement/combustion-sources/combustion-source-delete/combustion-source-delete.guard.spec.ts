import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { CombustionSourceDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/combustion-sources/combustion-source-delete/combustion-source-delete.guard';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('CombustionSourceDeleteGuard', () => {
  let guard: CombustionSourceDeleteGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(CombustionSourceDeleteGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate when combustionSource exists', async () => {
    activatedRouteSnapshot.params = { taskId: 1, combustionSource: 'Refinery fuel' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to `combustion-sources` when combustionSource does not exist', async () => {
    activatedRouteSnapshot.params = { taskId: 1, combustionSource: 'non existing combustion source' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/opinion-statement/combustion-sources'));
  });
});
