import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { AerModule } from '../../../aer.module';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockPostBuild, mockStateBuild } from '../../testing/mock-state';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { TiersUsedComponent } from './tiers-used.component';

describe('TiersUsedComponent', () => {
  let page: Page;
  let router: Router;
  let component: TiersUsedComponent;
  let fixture: ComponentFixture<TiersUsedComponent>;

  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  class Page extends BasePage<TiersUsedComponent> {
    get activityDataRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="ACTIVITY_DATA"]');
    }
    get emissionFactorRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="EMISSION_FACTOR"]');
    }
    get oxidationFactorRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="OXIDATION_FACTOR"]');
    }
    get netCalorificValueRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="NET_CALORIFIC_VALUE"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TiersUsedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, AerModule, RouterTestingModule, CalculationEmissionsModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for adding a new source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterMonitoringTiers: [
                      {
                        tier: 'NO_TIER',
                        type: 'EMISSION_FACTOR',
                      },
                      {
                        type: 'ACTIVITY_DATA',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'NET_CALORIFIC_VALUE',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'OXIDATION_FACTOR',
                      },
                    ] as any,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual(['You must select a suitable tier']);

      page.submitButton.click();
      fixture.detectChanges();

      page.activityDataRadios[1].click();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterCalculationMethod: {
                      type: 'MANUAL',
                    },
                    parameterMonitoringTiers: [
                      {
                        tier: 'TIER_3',
                        type: 'ACTIVITY_DATA',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'EMISSION_FACTOR',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'NET_CALORIFIC_VALUE',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'OXIDATION_FACTOR',
                      },
                    ] as any,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-reason'], { relativeTo: route });
    });
  });

  describe('for editing a source stream emission', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproachEmissions: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions,
              CALCULATION_CO2: {
                ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
                sourceStreamEmissions: [
                  {
                    parameterMonitoringTiers: [
                      {
                        tier: 'NO_TIER',
                        type: 'EMISSION_FACTOR',
                      },
                      {
                        tier: 'TIER_3',
                        type: 'ACTIVITY_DATA',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'NET_CALORIFIC_VALUE',
                      },
                      {
                        tier: 'NO_TIER',
                        type: 'OXIDATION_FACTOR',
                      },
                    ] as any,
                  },
                ],
              },
            },
          },
          {
            CALCULATION_CO2: [],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.activityDataRadios.length).toEqual(5);
      expect(page.activityDataRadios[0].checked).toBeFalsy();
      expect(page.activityDataRadios[1].checked).toBeTruthy();
      expect(page.activityDataRadios[2].checked).toBeFalsy();
      expect(page.activityDataRadios[3].checked).toBeFalsy();
      expect(page.activityDataRadios[4].checked).toBeFalsy();

      expect(page.emissionFactorRadios.length).toEqual(6);
      expect(page.emissionFactorRadios[0].checked).toBeFalsy();
      expect(page.emissionFactorRadios[1].checked).toBeFalsy();
      expect(page.emissionFactorRadios[2].checked).toBeFalsy();
      expect(page.emissionFactorRadios[3].checked).toBeFalsy();
      expect(page.emissionFactorRadios[4].checked).toBeFalsy();
      expect(page.emissionFactorRadios[5].checked).toBeTruthy();

      expect(page.oxidationFactorRadios.length).toEqual(4);
      expect(page.oxidationFactorRadios[0].checked).toBeFalsy();
      expect(page.oxidationFactorRadios[1].checked).toBeFalsy();
      expect(page.oxidationFactorRadios[2].checked).toBeFalsy();

      expect(page.netCalorificValueRadios.length).toEqual(5);
      expect(page.netCalorificValueRadios[0].checked).toBeFalsy();
      expect(page.netCalorificValueRadios[1].checked).toBeFalsy();
      expect(page.netCalorificValueRadios[2].checked).toBeFalsy();
      expect(page.netCalorificValueRadios[3].checked).toBeFalsy();
      expect(page.netCalorificValueRadios[4].checked).toBeTruthy();
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../tiers-reason'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
