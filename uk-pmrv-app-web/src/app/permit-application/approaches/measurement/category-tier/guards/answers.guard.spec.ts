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
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let guard: AnswersGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'MEASUREMENT_CO2_Biomass_Fraction' };

  const emissionPointCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  ).emissionPointCategoryAppliedTiers[0].emissionPointCategory;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        AnswersGuard,
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

  it('should redirect to biomass-fraction if task cannot start yet', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory: [],
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
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/measurement/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction`,
      ),
    );
  });

  it('should redirect to biomass-fraction if task not started', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory: emissionPointCategory,
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
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/measurement/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction`,
      ),
    );
  });

  it('should allow if wizard is complete', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory: emissionPointCategory,
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
    ).resolves.toEqual(true);
  });

  it('should redirect to summary page if task completed', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            MEASUREMENT_CO2: {
              emissionPointCategoryAppliedTiers: [
                {
                  emissionPointCategory: emissionPointCategory,
                  biomassFraction: {
                    exist: true,
                    tier: 'TIER_1',
                    isHighestRequiredTier: true,
                    standardReferenceSource: {
                      applyDefaultValue: true,
                      defaultValue: 'test',
                      type: 'BRITISH_CERAMIC_CONFEDERATION',
                    },
                  },
                },
              ],
            },
          },
        },
        {
          MEASUREMENT_CO2_Category: [true],
          MEASUREMENT_CO2_Biomass_Fraction: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/measurement/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction/summary`,
      ),
    );
  });
});
