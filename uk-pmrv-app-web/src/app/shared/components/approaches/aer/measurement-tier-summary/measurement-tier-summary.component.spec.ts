import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { mockClass } from '@testing';

import { AerApplicationSubmitRequestTaskPayload, MeasurementOfCO2Emissions, ReportingDataService } from 'pmrv-api';

import { MeasurementTierSummaryComponent } from './measurement-tier-summary.component';

describe('MeasurementTierSummaryComponent', () => {
  let component: MeasurementTierSummaryComponent;
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
      emissionPoints: [
        {
          id: '900',
          reference: 'EP1',
          description: 'west side chimney',
        },
        {
          id: '901',
          reference: 'EP2',
          description: 'east side chimney',
        },
      ],
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
        MEASUREMENT_CO2: {
          type: 'MEASUREMENT_CO2',
          hasTransfer: true,
          emissionPointEmissions: [
            {
              id: 'f61d8b13-74c4-4da8-b59c-368f82d955fb',
              transfer: {
                transferDirection: 'EXPORTED_TO_LONG_TERM_FACILITY',
                installationEmitter: {
                  email: 'permitsubmit1@trasys.gr',
                  emitterId: '34',
                },
                entryAccountingForTransfer: true,
                installationDetailsType: 'INSTALLATION_EMITTER',
                transferType: 'TRANSFER_CO2',
              } as any,
              annualGasFlow: '8',
              durationRange: {
                fullYearCovered: true,
              },
              tier: 'TIER_3',
              emissionPoint: '900',
              sourceStreams: ['324'],
              emissionSources: ['853'],
              biomassPercentages: {
                contains: false,
              },
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'reason',
              },
              calculationCorrect: true,
              reportableEmissions: '37.026',
              globalWarmingPotential: '1',
              sustainableBiomassEmissions: '0.0',
              annualHourlyAverageGHGConcentration: '33',
              annualHourlyAverageFlueGasFlow: '34',
              measurementAdditionalInformation: {},
              annualFossilAmountOfGreenhouseGas: '37.026',
            },
          ],
        } as MeasurementOfCO2Emissions,
      },
      regulatedActivities: undefined,
      aerMonitoringPlanDeviation: undefined,
    },
    permitOriginatedData: {
      permitType: 'GHGE',
      installationCategory: 'A_LOW_EMITTER',
      permitMonitoringApproachMonitoringTiers: {
        measurementCO2EmissionPointParamMonitoringTiers: {
          'f61d8b13-74c4-4da8-b59c-368f82d955fb': 'TIER_3',
        },
      },
    },
  } as AerApplicationSubmitRequestTaskPayload;

  @Component({
    template: `
      <app-measurement-tier-summary
        [isEditable]="isEditable"
        [payload]="payload"
        [taskKey]="'MEASUREMENT_CO2'"
        [index]="0"></app-measurement-tier-summary>
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
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(MeasurementTierSummaryComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary details', () => {
    expect(headings()).toEqual([
      'Emission network',
      'Transferred CO2',
      'Tiers',
      'Reason for not using monitoring plan tiers',
      'Measurement data',
      'Calculated emissions',
    ]);
    expect(summaryListValues()).toEqual([
      ['Emission point', ['EP1 west side chimney']],
      ['Source streams', ['the reference Anthracite']],
      ['Emission sources', ['emission source 1 reference emission source 1 description']],
      ['Does the source stream contain biomass?', ['No']],
      ['Date range for this entry', ['the whole year']],
      ['Are the emissions from this source stream exported to, or received from another installation?', ['Yes']],
      [
        'What direction is the transferred CO2 travelling?',
        ['Exported to a long-term geological storage related facility'],
      ],
      ['Installation emitter ID', ['34']],
      ['Contact email address', ['permitsubmit1@trasys.gr']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 3']],
      ['Reason', ['Due to a data gap  reason']],
      ['Annual hourly average amount of CO2 in the flue gas', ['33 hours']],
      ['Annual hourly average flue gas flow', ['34 (1000/Nm3)']],
      ['Annual flue gas flow', ['8 (1000/Nm3)', '']],
      ['Global warming potential of the greenhouse gas', ['1 tCO2/GHG', '']],
      ['Annual fossil amount of greenhouse gas', ['37.026 tonnes', '']],
      ['Reportable emissions', ['37.026 tonnes CO2e', '']],
      ['Sustainable biomass', ['0 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes']],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Emission point', ['EP1 west side chimney', 'Change']],
      ['Source streams', ['the reference Anthracite', 'Change']],
      ['Emission sources', ['emission source 1 reference emission source 1 description', 'Change']],
      ['Does the source stream contain biomass?', ['No', 'Change']],
      ['Date range for this entry', ['the whole year', 'Change']],
      [
        'Are the emissions from this source stream exported to, or received from another installation?',
        ['Yes', 'Change'],
      ],
      [
        'What direction is the transferred CO2 travelling?',
        ['Exported to a long-term geological storage related facility', 'Change'],
      ],
      ['Installation emitter ID', ['34', 'Change']],
      ['Contact email address', ['permitsubmit1@trasys.gr', 'Change']],
      ['Tier applied in the monitoring plan', ['Tier 3', '']],
      ['Tier used', ['Tier 3', 'Change']],
      ['Reason', ['Due to a data gap  reason', 'Change']],
      ['Annual hourly average amount of CO2 in the flue gas', ['33 hours', 'Change']],
      ['Annual hourly average flue gas flow', ['34 (1000/Nm3)', 'Change']],
      ['Annual flue gas flow', ['8 (1000/Nm3)', '']],
      ['Global warming potential of the greenhouse gas', ['1 tCO2/GHG', '']],
      ['Annual fossil amount of greenhouse gas', ['37.026 tonnes', '']],
      ['Reportable emissions', ['37.026 tonnes CO2e', '']],
      ['Sustainable biomass', ['0 tonnes CO2e', '']],
      ['Are the calculated emissions correct?', ['Yes', 'Change']],
    ]);
  });
});
