import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { mockStateBuild } from '@tasks/air/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovementResponseGuard } from './air-improvement-response.guard';

describe('AirImprovementResponseGuard', () => {
  let guard: AirImprovementResponseGuard;
  let router: Router;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, AirTaskSharedModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(AirImprovementResponseGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it("should activate when type is YES and path is 'improvement-positive'", async () => {
    store.setState(
      mockStateBuild({
        operatorImprovementResponses: {
          '1': {
            type: 'YES',
          },
        },
      }),
    );
    const activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.url = [new UrlSegment('improvement-positive', {})];
    activatedRouteSnapshot.params = { taskId: 1, id: '1' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it("should activate when type is NO and path is 'improvement-negative'", async () => {
    store.setState(
      mockStateBuild({
        operatorImprovementResponses: {
          '1': {
            type: 'NO',
          },
        },
      }),
    );
    const activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.url = [new UrlSegment('improvement-negative', {})];
    activatedRouteSnapshot.params = { taskId: 1, id: '1' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it("should activate when type is ALREADY_MADE and path is 'improvement-existing'", async () => {
    store.setState(
      mockStateBuild({
        operatorImprovementResponses: {
          '1': {
            type: 'ALREADY_MADE',
          },
        },
      }),
    );
    const activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.url = [new UrlSegment('improvement-existing', {})];
    activatedRouteSnapshot.params = { taskId: 1, id: '1' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect when type is different than last url fragment', async () => {
    store.setState(
      mockStateBuild({
        operatorImprovementResponses: {
          '1': {
            type: 'YES',
          },
        },
      }),
    );
    const activatedRouteSnapshot = new ActivatedRouteSnapshot();
    activatedRouteSnapshot.url = [new UrlSegment('improvement-negative', {})];
    activatedRouteSnapshot.params = { taskId: 1, id: '1' };

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/tasks/1/air/submit'));
  });
});
