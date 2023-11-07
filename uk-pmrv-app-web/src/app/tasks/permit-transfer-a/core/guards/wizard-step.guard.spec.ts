import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { mockState, mockStateBuild } from '@tasks/permit-transfer-a/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { WizardStepGuard } from './wizard-step.guard';

describe('WizardStepGuard', () => {
  let guard: WizardStepGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 13 };

  const routerStateSnapshot = (step?: string) => {
    return {
      url: `/tasks/${activatedRouteSnapshot.params.taskId}/permit-transfer-a/submit/${step ? step : 'date'}`,
    } as RouterStateSnapshot;
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [KeycloakService, HttpClient, HttpHandler],
    });
    guard = TestBed.inject(WizardStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    store.setState({ ...mockState });
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should navigate to next step', async () => {
    store.setState({ ...mockStateBuild({ transferCode: undefined }) });

    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('date')) as Observable<true | UrlTree>,
      ),
    ).resolves.toEqual(true);
  });

  it('should return to first page when wizard is not completed', async () => {
    store.setState({ ...mockStateBuild({ transferCode: undefined }) });

    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshot, routerStateSnapshot('summary')) as Observable<true | UrlTree>,
      ),
    ).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-transfer-a/submit/reason`),
    );
  });

  it('should return to summary page when section is completed', async () => {
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot()) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-transfer-a/submit/summary`),
    );
  });

  it('should return true', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: {
        state: {
          changing: true,
        },
      },
    } as any);

    expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot()) as Observable<true | UrlTree>).toEqual(
      true,
    );
  });
});
