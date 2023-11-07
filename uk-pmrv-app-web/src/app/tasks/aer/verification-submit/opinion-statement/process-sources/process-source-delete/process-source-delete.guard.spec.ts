import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { ProcessSourceDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/process-sources/process-source-delete/process-source-delete.guard';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('ProcessSourceDeleteGuard', () => {
  let guard: ProcessSourceDeleteGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(ProcessSourceDeleteGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate when processSource exists', async () => {
    activatedRouteSnapshot.params = { taskId: 1, processSource: 'Calcination of lime' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to `process-sources` when processSource does not exist', async () => {
    activatedRouteSnapshot.params = { taskId: 1, processSource: 'non existing process source' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/opinion-statement/process-sources'));
  });
});
