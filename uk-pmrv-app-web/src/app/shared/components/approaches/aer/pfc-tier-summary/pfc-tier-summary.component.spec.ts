import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { mockClass } from '@testing';

import { AerApplicationSubmitRequestTaskPayload, ReportingDataService } from 'pmrv-api';

import { SharedModule } from '../../../../shared.module';
import { PfcTierSummaryComponent } from './pfc-tier-summary.component';
describe('PfcTierSummaryComponent', () => {
  let component: PfcTierSummaryComponent;
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
        CALCULATION_PFC: {
          type: 'CALCULATION_PFC',
          sourceStreamEmissions: [
            {
              id: '816528ef-c303-4a95-9d21-9199264672e8',
              amountOfCF4: 0.204,
              amountOfC2F6: 27.744,
              sourceStream: '324',
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['853'],
              totalCF4Emissions: 1507.56,
              calculationCorrect: true,
              totalC2F6Emissions: 338476.8,
              reportableEmissions: 11332812.0,
              totalPrimaryAluminium: 34,
              massBalanceApproachUsed: true,
              parameterMonitoringTier: {
                activityDataTier: 'TIER_1',
                emissionFactorTier: 'TIER_1',
              },
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'erte',
              },
              pfcSourceStreamEmissionCalculationMethodData: {
                calculationMethod: 'SLOPE',
                c2F6WeightFraction: 4,
                anodeEffectsPerCellDay: 1,
                slopeCF4EmissionFactor: 3,
                percentageOfCollectionEfficiency: 3,
                averageDurationOfAnodeEffectsInMinutes: 2,
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
        calculationPfcSourceStreamParamMonitoringTiers: {
          '816528ef-c303-4a95-9d21-9199264672e8': {
            activityDataTier: 'TIER_3',
            emissionFactorTier: 'TIER_2',
          },
        },
      },
      installationCategory: 'A',
    },
  } as AerApplicationSubmitRequestTaskPayload;

  @Component({
    template: `
      <app-pfc-tier-summary [isEditable]="isEditable" [payload]="payload" [index]="0"></app-pfc-tier-summary>
    `,
  })
  class TestComponent {
    isEditable = false;
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
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(PfcTierSummaryComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(headings()).toEqual([
      'Emission network',
      'Reason for not using monitoring plan tiers',
      'Activity data',
      'Emission factor',
      'Calculation data',
      'CF4 (Tetrafluoromethane)',
      'C2F6 (Hexafluoroethane)',
      'Calculated emissions',
    ]);
    expect(summaryListValues()).toEqual([
      ['Source stream', ['the reference Anthracite']],
      ['Emission sources', ['emission source 1 reference emission source 1 description']],
      ['Method to calculate emissions', ['Method A (Slope)']],
      ['Are you using a mass balance approach to identify the activity data?', ['Yes']],
      ['Date range for this entry', ['the whole year']],
      ['Reason', ['Due to a data gap  erte']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 1']],
      ['Total production of primary aluminium', ['34 tonnes']],
      ['Tier applied in the monitoring plan', ['Tier 2', '']],
      ['Tier used', ['Tier 1']],
      ['Amount of anode effects per cell-day', ['1']],
      ['Average duration of anode effects, in minutes', ['2']],
      ['Slope emission factor of CF4', ['3']],
      ['Weight fraction of C2F6', ['4']],
      ['Collection efficiency', ['3']],
      ['Amount of CF4', ['0.204', '']],
      ['Global warming potential', ['7390 tonnes CO2 / tonnes CF4', '']],
      ['Total CF4 emissions', ['1507.56', '']],
      ['Amount of C2F6', ['27.744', '']],
      ['Global warming potential', ['12,000 tonnes CO2/ tonnes C2F6', '']],
      ['Total CF4 emissions', ['338476.8', '']],
      ['Reportable emissions', ['11332812 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes']],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Source stream', ['the reference Anthracite', 'Change']],
      ['Emission sources', ['emission source 1 reference emission source 1 description', 'Change']],
      ['Method to calculate emissions', ['Method A (Slope)', 'Change']],
      ['Are you using a mass balance approach to identify the activity data?', ['Yes', 'Change']],
      ['Date range for this entry', ['the whole year', 'Change']],
      ['Reason', ['Due to a data gap  erte', 'Change']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 1', 'Change']],
      ['Total production of primary aluminium', ['34 tonnes', 'Change']],
      ['Tier applied in the monitoring plan', ['Tier 2', '']],
      ['Tier used', ['Tier 1', 'Change']],
      ['Amount of anode effects per cell-day', ['1', 'Change']],
      ['Average duration of anode effects, in minutes', ['2', 'Change']],
      ['Slope emission factor of CF4', ['3', 'Change']],
      ['Weight fraction of C2F6', ['4', 'Change']],
      ['Collection efficiency', ['3', 'Change']],
      ['Amount of CF4', ['0.204', '']],
      ['Global warming potential', ['7390 tonnes CO2 / tonnes CF4', '']],
      ['Total CF4 emissions', ['1507.56', '']],
      ['Amount of C2F6', ['27.744', '']],
      ['Global warming potential', ['12,000 tonnes CO2/ tonnes C2F6', '']],
      ['Total CF4 emissions', ['338476.8', '']],
      ['Reportable emissions', ['11332812 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes', 'Change']],
    ]);
  });
});
