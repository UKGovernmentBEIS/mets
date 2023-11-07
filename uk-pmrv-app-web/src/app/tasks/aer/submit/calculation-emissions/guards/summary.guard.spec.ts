import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let store: CommonTasksStore;
  let guard: SummaryGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };

  const sourceStreamEmission = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
    .sourceStreamEmissions[0];

  const parameterMonitoringTiers = sourceStreamEmission.parameterMonitoringTiers;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [KeycloakService, HttpClient, HttpHandler],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to first step  if wizard is not complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            CALCULATION_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
              sourceStreamEmissions: [
                {
                  parameterMonitoringTiers,
                },
              ],
            },
          },
        },
        {
          CALCULATION_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/aer/submit/calculation-emissions/0/emission-network`));
  });
  it('should allow if wizard is complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            CALCULATION_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
              sourceStreamEmissions: [
                {
                  ...sourceStreamEmission,
                },
              ],
            },
          },
        },
        {
          CALCULATION_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});
