import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationEmissionsTierSummaryComponent } from '@shared/components/approaches/aer/calculation-emissions-tier/calculation-emissions-tier-summary.component';
import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationActivityDataMonitoringTier,
  CalculationBiomassFractionMonitoringTier,
  CalculationNetCalorificValueMonitoringTier,
  CalculationOxidationFactorMonitoringTier,
  ReportingDataService,
} from 'pmrv-api';

describe('CalculationEmissionsTierSummaryComponent', () => {
  let component: CalculationEmissionsTierSummaryComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  const reportingService = mockClass(ReportingDataService);

  function summaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list__row')).map((row) => [
      row.querySelector('dt').textContent.trim(),
      Array.from(row.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }

  function headings() {
    return Array.from(element.querySelectorAll<HTMLHeadingElement>('h2')).map((el) => el.textContent.trim());
  }

  const mockPayload = {
    payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
    aer: {
      naceCodes: undefined,
      abbreviations: undefined,
      additionalDocuments: undefined,
      confidentialityStatement: undefined,
      sourceStreams: [
        {
          id: '324',
          description: 'ANTHRACITE',
          type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
          reference: 'the reference',
        },
        {
          id: '325',
          description: 'BIODIESELS',
          type: 'CEMENT_CLINKER_CKD',
          reference: 'the other reference',
        },
      ],
      emissionSources: [
        {
          id: '853',
          description: 'emission source 1 description',
          reference: 'emission source 1 reference',
        },
        {
          id: '854',
          description: 'emission source 2 description',
          reference: 'emission source 2 reference',
        },
      ],
      pollutantRegisterActivities: undefined,
      monitoringApproachEmissions: {
        CALCULATION_CO2: {
          type: 'CALCULATION_CO2',
          hasTransfer: true,
          sourceStreamEmissions: [
            {
              id: 'permit-324',
              transfer: {
                entryAccountingForTransfer: false,
              },
              sourceStream: '324',
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['853'],
              biomassPercentages: {
                contains: true,
                biomassPercentage: 50,
                nonSustainableBiomassPercentage: 50,
              },
              parameterMonitoringTiers: [
                {
                  tier: 'TIER_2A',
                  type: 'EMISSION_FACTOR',
                },
                {
                  tier: 'TIER_2A',
                  type: 'NET_CALORIFIC_VALUE',
                },
                {
                  tier: 'TIER_2',
                  type: 'ACTIVITY_DATA',
                },
                {
                  tier: 'TIER_3',
                  type: 'OXIDATION_FACTOR',
                },
                {
                  tier: 'TIER_2',
                  type: 'BIOMASS_FRACTION',
                },
              ],
              parameterCalculationMethod: {
                type: 'REGIONAL_DATA',
                postCode: 'SW17 9',
                localZoneCode: 'SE',
                fuelMeteringConditionType: 'CELSIUS_15',
                emissionCalculationParamValues: {
                  emissionFactor: '55.9245',
                  oxidationFactor: '1',
                  calculationFactor: '0.9476',
                  efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
                  netCalorificValue: '0.035109199999999788',
                  calculationCorrect: true,
                  ncvMeasurementUnit: 'GJ_PER_NM3',
                  totalReportableEmissions: '1.96346',
                  totalSustainableBiomassEmissions: '1.96346',
                },
                calculationActivityDataCalculationMethod: {
                  type: 'CONTINUOUS_METERING',
                  totalMaterial: '2000',
                  measurementUnit: 'NM3',
                  activityData: '1894',
                },
              },
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'Describe the reasons and methods used during the data gap',
              },
            },
          ],
        },
      },
      emissionPoints: undefined,
      regulatedActivities: undefined,
      aerMonitoringPlanDeviation: undefined,
    },
    permitOriginatedData: {
      permitType: 'GHGE',
      permitMonitoringApproachMonitoringTiers: {
        calculationSourceStreamParamMonitoringTiers: {
          'permit-324': [
            {
              tier: 'TIER_2B',
              type: 'NET_CALORIFIC_VALUE',
            } as CalculationNetCalorificValueMonitoringTier,
            {
              tier: 'NO_TIER',
              type: 'ACTIVITY_DATA',
            } as CalculationActivityDataMonitoringTier,
            {
              tier: 'TIER_2',
              type: 'OXIDATION_FACTOR',
            } as CalculationOxidationFactorMonitoringTier,
            {
              tier: 'TIER_3',
              type: 'BIOMASS_FRACTION',
            } as CalculationBiomassFractionMonitoringTier,
          ],
        },
      },
      installationCategory: 'A',
    },
  } as AerApplicationSubmitRequestTaskPayload;

  @Component({
    template: `
      <app-calculation-emissions-tier-summary
        [isEditable]="isEditable"
        [payload]="payload"
        [index]="0"
        [areTiersExtraConditionsMet]="areTiersExtraConditionsMet"></app-calculation-emissions-tier-summary>
    `,
  })
  class TestComponent {
    isEditable = false;
    areTiersExtraConditionsMet = true;
    payload = mockPayload;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: ReportingDataService, useValue: reportingService }],
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    reportingService.getChargingZonesByPostCode.mockReturnValueOnce(
      of([
        {
          name: 'South east',
          code: 'SE',
        },
      ]),
    );

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(CalculationEmissionsTierSummaryComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(headings()).toEqual([
      'Emission network',
      'Transferred CO2',
      'Parameter values',
      'Reason for not using monitoring plan tiers',
      'Emission factor',
      'Net calorific value',
      'Activity data',
      'Oxidation factor',
      'Biomass',
      'Calculated emissions',
    ]);
    expect(summaryListValues()).toEqual([
      ['Source stream', ['the reference Anthracite']],
      ['Emission sources', ['emission source 1 reference emission source 1 description']],
      ['Does the source stream contain biomass?', ['Yes']],
      ['Date range for this entry', ['the whole year']],
      ['Are the emissions from this source stream exported to, or received from another installation?', ['No']],
      ['Calculation method', ['Use regional data for natural gas']],
      ['Local delivery zone', ['South east']],
      ['What conditions was the gas for this source stream metered?', ['15º celsius (metering condition)']],
      ['Reason', ['Due to a data gap  Describe the reasons and methods used during the data gap']],
      ['Tier used', ['Tier 2a']],
      ['Value', ['55.9245 tCO2/TJ']],
      ['Tier applied in the monitoring plan', ['Tier 2b', '']],
      ['Tier used', ['Tier 2a']],
      ['Value', ['0.035109199999999788 GJ/Nm3']],
      ['Tier applied in the monitoring plan', ['No tier', '']],
      ['Tier used', ['Tier 2']],
      ['How do you want to calculate the activity data for this source stream?', ['Continuous metering']],
      ['Total fuel or material used', ['2000 normal cubic meter (Nm3)']],
      ['Value adjusted to 0ºC standard conditions', ['1894', '']],
      ['Metering coefficient used for the adjustment', ['0.9476', '']],
      ['Tier applied in the monitoring plan', ['Tier 2', '']],
      ['Tier used', ['Tier 3']],
      ['Value', ['1']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 2']],
      ['Biomass percentage', ['50%']],
      ['Non sustainable biomass percentage', ['50%']],
      ['Reportable emissions', ['1.96346 tonnes CO2e', '']],
      ['Sustainable biomass', ['1.96346 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes']],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Source stream', ['the reference Anthracite', 'Change']],
      ['Emission sources', ['emission source 1 reference emission source 1 description', 'Change']],
      ['Does the source stream contain biomass?', ['Yes', 'Change']],
      ['Date range for this entry', ['the whole year', 'Change']],
      [
        'Are the emissions from this source stream exported to, or received from another installation?',
        ['No', 'Change'],
      ],
      ['Calculation method', ['Use regional data for natural gas', 'Change']],
      ['Local delivery zone', ['South east', 'Change']],
      ['What conditions was the gas for this source stream metered?', ['15º celsius (metering condition)', 'Change']],
      ['Reason', ['Due to a data gap  Describe the reasons and methods used during the data gap', 'Change']],
      ['Tier used', ['Tier 2a', 'Change']],
      ['Value', ['55.9245 tCO2/TJ', '']],
      ['Tier applied in the monitoring plan', ['Tier 2b', '']],
      ['Tier used', ['Tier 2a', 'Change']],
      ['Value', ['0.035109199999999788 GJ/Nm3', '']],
      ['Tier applied in the monitoring plan', ['No tier', '']],
      ['Tier used', ['Tier 2', 'Change']],
      ['How do you want to calculate the activity data for this source stream?', ['Continuous metering', 'Change']],
      ['Total fuel or material used', ['2000 normal cubic meter (Nm3)', 'Change']],
      ['Value adjusted to 0ºC standard conditions', ['1894', '']],
      ['Metering coefficient used for the adjustment', ['0.9476', '']],
      ['Tier applied in the monitoring plan', ['Tier 2', '']],
      ['Tier used', ['Tier 3', 'Change']],
      ['Value', ['1', '']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 2', 'Change']],
      ['Biomass percentage', ['50%', 'Change']],
      ['Non sustainable biomass percentage', ['50%', 'Change']],
      ['Reportable emissions', ['1.96346 tonnes CO2e', '']],
      ['Sustainable biomass', ['1.96346 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes', 'Change']],
    ]);
  });
});
