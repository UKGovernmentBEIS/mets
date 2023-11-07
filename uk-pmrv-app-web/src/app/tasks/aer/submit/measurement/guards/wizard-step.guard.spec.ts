import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { WizardStepGuard } from './wizard-step.guard';

describe('WizardStepGuard', () => {
  let store: CommonTasksStore;
  let guard: WizardStepGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };
  activatedRouteSnapshot.data = { taskKey: 'MEASUREMENT_CO2' };

  const emissionPointEmission = (mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2 as any)
    .emissionPointEmissions[0];

  const emissionSources = emissionPointEmission.emissionSources;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [KeycloakService, HttpClient, HttpHandler],
    });
    guard = TestBed.inject(WizardStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task needs review and current wizard step is first step', async () => {
    const routerStateSnapshot = {
      url: '/tasks/1/aer/submit/measurement-co2/0/emission-network',
    } as RouterStateSnapshot;

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            MEASUREMENT_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
              emissionPointEmissions: [
                {
                  emissionSources,
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to the first step  if task needs review and current wizard step is NOT the first step', async () => {
    const routerStateSnapshot = {
      url: '/tasks/1/aer/submit/measurement-co2/0/date-range',
    } as RouterStateSnapshot;

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            MEASUREMENT_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
              emissionPointEmissions: [
                {
                  emissionSources,
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/aer/submit/measurement-co2/0/emission-network`));
  });

  it('should activate if task is in progress', async () => {
    const routerStateSnapshot = {
      url: '/tasks/1/aer/submit/measurement/0/date-range',
    } as RouterStateSnapshot;

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            MEASUREMENT_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
              emissionPointEmissions: [
                {
                  emissionPoint: '900',
                  sourceStreams: ['324'],
                  emissionSources: ['853'],
                  biomassPercentages: {
                    contains: false,
                  },
                  globalWarmingPotential: 1,
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary if wizard is complete ', async () => {
    const routerStateSnapshot = {
      url: '/tasks/1/aer/submit/measurement-co2/0/date-range',
    } as RouterStateSnapshot;

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            MEASUREMENT_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.MEASUREMENT_CO2,
              emissionPointEmissions: [
                {
                  ...emissionPointEmission,
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/aer/submit/measurement-co2/0/summary`));
  });
});
