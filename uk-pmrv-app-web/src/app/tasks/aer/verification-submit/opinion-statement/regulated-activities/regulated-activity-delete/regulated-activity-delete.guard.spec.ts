import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RegulatedActivityDeleteGuard } from '@tasks/aer/verification-submit/opinion-statement/regulated-activities/regulated-activity-delete/regulated-activity-delete.guard';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('RegulatedActivityDeleteGuard', () => {
  let guard: RegulatedActivityDeleteGuard;
  let router: Router;
  let store: CommonTasksStore;
  let activatedRouteSnapshot: ActivatedRouteSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(RegulatedActivityDeleteGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    activatedRouteSnapshot = new ActivatedRouteSnapshot();
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate when activityType exists', async () => {
    activatedRouteSnapshot.params = { taskId: 1, activityType: 'COMBUSTION' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to `regulated-activities` when activityType does not exist', async () => {
    activatedRouteSnapshot.params = { taskId: 1, activityType: 'non existing activityType' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/aer/verification-submit/opinion-statement/regulated-activities'));
  });
});
