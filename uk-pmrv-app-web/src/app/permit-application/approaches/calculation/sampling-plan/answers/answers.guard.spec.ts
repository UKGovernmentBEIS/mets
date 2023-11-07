import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { CalculationOfCO2MonitoringApproach, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../../../testing';
import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let store: PermitApplicationStore<PermitApplicationState>;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('answers', null)];
  const routerStateSnapshot = {
    url: '/permit-issuance/276/calculation/sampling-plan/answers',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        AnswersGuard,
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { CALCULATION_CO2_Plan: [true] }));

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/calculation/sampling-plan/summary'),
    );
  });

  it('should allow if all step are completed', async () => {
    store.setState(mockStateBuild(undefined, { CALCULATION_CO2_Plan: [false] }));

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);
  });

  it('should redirect to start if at least one step is not completed', async () => {
    const mockCalculation = mockState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach;

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            type: 'CALCULATION_CO2',
          },
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/calculation/sampling-plan'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            ...mockCalculation,
            samplingPlan: {
              exist: false,
            },
          } as CalculationOfCO2MonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                procedurePlan: {
                  ...mockCalculation.samplingPlan.details.procedurePlan,
                },
              },
            },
          } as CalculationOfCO2MonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/calculation/sampling-plan'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                analysis: { ...mockCalculation.samplingPlan.details.analysis },
                procedurePlan: { ...mockCalculation.samplingPlan.details.procedurePlan },
                appropriateness: { ...mockCalculation.samplingPlan.details.appropriateness },
              },
            },
          } as CalculationOfCO2MonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-issuance/276/calculation/sampling-plan'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                analysis: { ...mockCalculation.samplingPlan.details.analysis },
                procedurePlan: { ...mockCalculation.samplingPlan.details.procedurePlan },
                appropriateness: { ...mockCalculation.samplingPlan.details.appropriateness },
                yearEndReconciliation: { exist: false },
              },
            },
          } as CalculationOfCO2MonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);
  });
});
