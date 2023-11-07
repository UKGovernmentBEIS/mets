import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ReportingService, SourceStreamCalculationParametersInfo, TasksService } from 'pmrv-api';

import { AerModule } from '../../../aer.module';
import { AerService } from '../../../core/aer.service';
import { mockAerApplyPayload } from '../../testing/mock-aer-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { CalculationEmissionsModule } from '../calculation-emissions.module';
import { ManualCalculationValuesComponent } from './manual-calculation-values.component';

describe('ManualCalculationValuesComponent', () => {
  let page: Page;
  let router: Router;

  let component: ManualCalculationValuesComponent;
  let fixture: ComponentFixture<ManualCalculationValuesComponent>;

  let store: CommonTasksStore;
  let aerService: AerService;
  let reportingService: ReportingService;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, null);

  const parameterMonitoringTiers = (mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2 as any)
    .sourceStreamEmissions[0].parameterMonitoringTiers;

  const parameterCalculationMethod = {
    type: 'MANUAL',
    calculationActivityDataCalculationMethod: {
      type: 'CONTINUOUS_METERING',
      totalMaterial: 4,
      measurementUnit: 'TONNES',
    },
  };

  const emissionCalculationParamValues = {
    efMeasurementUnit: 'TONNES_OF_CO2_PER_TONNE',
    emissionFactor: '20',
    ncvMeasurementUnit: 'GJ_PER_TONNE',
    netCalorificValue: '10',
    oxidationFactor: '30',
    providedEmissions: null,
    totalReportableEmissions: '50',
    totalSustainableBiomassEmissions: '10',
  };

  const calculationParameterTypes: SourceStreamCalculationParametersInfo = {
    applicableTypes: ['ACTIVITY_DATA', 'NET_CALORIFIC_VALUE', 'EMISSION_FACTOR', 'OXIDATION_FACTOR'],
    measurementUnitsCombinations: [
      {
        activityDataMeasurementUnit: 'NM3',
        efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
        ncvMeasurementUnit: 'GJ_PER_NM3',
      },
      {
        activityDataMeasurementUnit: 'NM3',
        efMeasurementUnit: 'TONNES_OF_CO2_PER_NM3',
        ncvMeasurementUnit: 'GJ_PER_NM3',
      },
      {
        activityDataMeasurementUnit: 'TONNES',
        efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
        ncvMeasurementUnit: 'GJ_PER_TONNE',
      },
      {
        activityDataMeasurementUnit: 'TONNES',
        efMeasurementUnit: 'TONNES_OF_CO2_PER_TONNE',
        ncvMeasurementUnit: 'GJ_PER_TONNE',
      },
    ],
  };

  const reportableEmissions = {
    reportableEmissions: emissionCalculationParamValues.totalReportableEmissions,
    sustainableBiomassEmissions: emissionCalculationParamValues.totalSustainableBiomassEmissions,
  };
  class Page extends BasePage<ManualCalculationValuesComponent> {
    get netCalorificValue() {
      return this.getInputValue('#netCalorificValue');
    }

    set netCalorificValue(value: string) {
      this.setInputValue('#netCalorificValue', value);
    }

    get emissionFactor() {
      return this.getInputValue('#emissionFactor');
    }

    set emissionFactor(value: string) {
      this.setInputValue('#emissionFactor', value);
    }

    get efMeasurementUnit() {
      return this.getInputValue('#efMeasurementUnit');
    }

    set efMeasurementUnit(value: string) {
      this.setInputValue('#efMeasurementUnit', value);
    }

    get oxidationFactor() {
      return this.getInputValue('#oxidationFactor');
    }

    set oxidationFactor(value: string) {
      this.setInputValue('#oxidationFactor', value);
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
    fixture = TestBed.createComponent(ManualCalculationValuesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);

    aerService = TestBed.inject(AerService);
    jest.spyOn(aerService, 'getCalculationParameterTypes').mockReturnValue(of(calculationParameterTypes));

    reportingService = TestBed.inject(ReportingService);

    jest.spyOn(reportingService, 'calculateEmissions').mockReturnValue(of(reportableEmissions));

    fixture.detectChanges();
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
                    parameterCalculationMethod,
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
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryLinks).toEqual([
        'Enter a value',
        'Please select a measurement unit',
        'Enter a value',
        'Enter a value',
      ]);

      page.netCalorificValue = emissionCalculationParamValues.netCalorificValue;
      page.emissionFactor = emissionCalculationParamValues.emissionFactor;
      page.efMeasurementUnit = `1: ${emissionCalculationParamValues.efMeasurementUnit}`;
      page.oxidationFactor = emissionCalculationParamValues.oxidationFactor;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(postTaskSaveSpy).toHaveBeenCalledWith(
        {
          monitoringApproachEmissions: {
            ...mockAerApplyPayload.aer.monitoringApproachEmissions,
            CALCULATION_CO2: {
              ...mockAerApplyPayload.aer.monitoringApproachEmissions.CALCULATION_CO2,
              sourceStreamEmissions: [
                {
                  parameterCalculationMethod: {
                    ...parameterCalculationMethod,
                    emissionCalculationParamValues: {
                      ...emissionCalculationParamValues,
                      calculationCorrect: null,
                    },
                  },
                  parameterMonitoringTiers,
                },
              ],
            },
          },
        },
        undefined,
        [false],
        'CALCULATION_CO2',
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../manual-data-review'], { relativeTo: route });
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
                    parameterCalculationMethod: {
                      ...parameterCalculationMethod,
                      emissionCalculationParamValues: {
                        ...emissionCalculationParamValues,
                        calculationCorrect: null,
                      },
                    },
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
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should  fill the form with data from the store', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.emissionFactor).toEqual(emissionCalculationParamValues.emissionFactor.toString());
      expect(page.efMeasurementUnit).toEqual(emissionCalculationParamValues.efMeasurementUnit);
      expect(page.netCalorificValue).toEqual(emissionCalculationParamValues.netCalorificValue.toString());
      expect(page.oxidationFactor).toEqual(emissionCalculationParamValues.oxidationFactor.toString());
    });

    it('should post a valid form and navigate to next step ', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      postTaskSaveSpy.mockReturnValue(of({}));

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../manual-data-review'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });
  });
});
