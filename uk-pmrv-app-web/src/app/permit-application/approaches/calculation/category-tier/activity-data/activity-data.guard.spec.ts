import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockStateBuild } from '../../../../testing/mock-state';
import { ActivityDataGuard } from './activity-data.guard';

describe('ActivityDataGuard', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let guard: ActivityDataGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'CALCULATION_CO2_Activity_Data' };

  const sourceStreamCategory = {
    sourceStream: '16236817394240.1574963093314663',
    emissionSources: ['16245246343280.27155194483385103'],
    emissionPoints: ['16236817394240.1574963093314663'],
    annualEmittedCO2Tonnes: '23.5',
    calculationMethod: 'OVERVOLTAGE',
    categoryType: 'MAJOR',
  };

  const activityData = {
    measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
    tier: 'TIER_4',
    uncertainty: 'LESS_OR_EQUAL_5_0',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [
        ActivityDataGuard,
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    });
    guard = TestBed.inject(ActivityDataGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task cannot start yet', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION_CO2: {
            sourceStreamCategoryAppliedTiers: [],
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if activity data does not exist', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION_CO2: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                },
              ],
            },
          },
        },
        {
          measurementDevicesOrMethods: [true],
          CALCULATION_CO2_Category: [true],
          CALCULATION_CO2_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to answers page if task in progress', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION_CO2: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                  activityData: activityData,
                },
              ],
            },
          },
        },
        {
          measurementDevicesOrMethods: [true],
          CALCULATION_CO2_Category: [true],
          CALCULATION_CO2_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/activity-data/answers`,
      ),
    );
  });

  it('should redirect to summary page if task completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION_CO2: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                  activityData: activityData,
                },
              ],
            },
          },
        },
        {
          measurementDevicesOrMethods: [true],
          CALCULATION_CO2_Category: [true],
          CALCULATION_CO2_Activity_Data: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-issuance/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/activity-data/summary`,
      ),
    );
  });
});
