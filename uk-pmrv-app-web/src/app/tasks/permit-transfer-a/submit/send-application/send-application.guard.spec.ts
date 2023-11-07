import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockState, mockStateBuild } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { SendApplicationGuard } from './send-application.guard';

describe('SendApplicationGuard', () => {
  let guard: SendApplicationGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 13 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [KeycloakService, HttpClient, HttpHandler],
    });

    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    guard = TestBed.inject(SendApplicationGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return to submit page', async () => {
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-transfer-a/submit`));
  });

  it('should return true', async () => {
    store.setState({ ...mockStateBuild({ sectionCompleted: true }) });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
