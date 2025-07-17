import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { WizardStepGuard } from './wizard-step.guard';

describe('WizardStepGuard', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let guard: WizardStepGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'MEASUREMENT_CO2_Biomass_Fraction' };

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        WizardStepGuard,
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(WizardStepGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task does not exist yet', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          MEASUREMENT_CO2: {
            emissionPointCategoryAppliedTiers: [],
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to answers page if wizard is complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory,
                  biomassFraction: {
                    exist: true,
                    tier: 'TIER_2B',
                    isHighestRequiredTier: true,
                    defaultValueApplied: true,
                    standardReferenceSource: {
                      type: 'MONITORING_REPORTING_REGULATION_ARTICLE_36_3',
                    },
                    analysisMethodUsed: false,
                  },
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2_Biomass_Fraction: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/measurement/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction/answers`,
      ),
    );
  });
});
