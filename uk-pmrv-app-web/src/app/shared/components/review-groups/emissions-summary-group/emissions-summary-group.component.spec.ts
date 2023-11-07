import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { Aer, CalculationOfPfcEmissions, FallbackEmissions } from 'pmrv-api';

import { EmissionsSummaryGroupComponent } from './emissions-summary-group.component';

describe('EmissionsSummaryGroupComponent', () => {
  let component: EmissionsSummaryGroupComponent;

  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: ` <app-emissions-summary-group [data]="data"></app-emissions-summary-group> `,
  })
  class TestComponent {
    data = {
      emissionSources: [],
      naceCodes: undefined,
      abbreviations: undefined,
      additionalDocuments: undefined,
      confidentialityStatement: undefined,
      sourceStreams: [
        {
          id: '16729074127320.4550922071395316',
          type: 'CEMENT_CLINKER_NON_CARBONATE_CARBON',
          reference: 'Reprehenderit conse',
          description: 'OTHER_BIOGAS',
        },
        {
          id: '16729084817200.8575541984135522',
          type: 'LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B',
          reference: 'Dolorem sunt ducimu',
          description: 'IMPORT_FUEL_GAS',
        },
        {
          id: '16733400082870.8307910794079321',
          type: 'COMBUSTION_COMMERCIAL_STANDARD_FUELS',
          reference: 'COMBUSTION_COMMERCIAL_STANDARD_FUELS',
          description: 'GAS_COKE',
        },
        {
          id: '16733400297060.8808995331842155',
          type: 'COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS',
          reference: 'COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS',
          description: 'FUEL_OIL',
        },
        {
          id: '16733400442910.7831379162812164',
          type: 'REFINERIES_HYDROGEN_PRODUCTION',
          reference: 'REFINERIES_HYDROGEN_PRODUCTION',
          description: 'ANTHRACITE',
        },
        {
          id: '16733400658630.7764452617776763',
          type: 'CEMENT_CLINKER_CKD',
          reference: 'CEMENT_CLINKER_CKD',
          description: 'ANTHRACITE',
        },
        {
          id: '16733400862540.3675914186257778',
          type: 'REFINERIES_MASS_BALANCE',
          reference: 'REFINERIES_MASS_BALANCE',
          description: 'ETHANE',
        },
        {
          id: '16733400977360.166880452262407',
          type: 'COKE_MASS_BALANCE',
          reference: 'COKE_MASS_BALANCE',
          description: 'COKE',
        },
      ],
      pollutantRegisterActivities: undefined,
      monitoringApproachEmissions: {
        CALCULATION_CO2: {
          type: 'CALCULATION_CO2',
          hasTransfer: true,
          sourceStreamEmissions: [
            {
              sourceStream: '16733400082870.8307910794079321',
              transfer: {
                transferDirection: 'RECEIVED_FROM_ANOTHER_INSTALLATION',
                installationDetails: {
                  city: 'Facilis placeat vel',
                  email: 'zikezy@mailinator.com',
                  line1: '913 East Green Nobel Drive',
                  line2: 'Eu qui culpa itaque',
                  postcode: 'Ipsa eiusmod quia m',
                  installationName: 'Autumn Rollins',
                },
                entryAccountingForTransfer: true,
                installationDetailsType: 'INSTALLATION_DETAILS',
              },
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['16703224856040.6939830628582251'],
              biomassPercentages: {
                contains: true,
                biomassPercentage: '30',
                nonSustainableBiomassPercentage: '60',
              },
              parameterMonitoringTiers: [
                {
                  tier: 'TIER_3',
                  type: 'OXIDATION_FACTOR',
                },
                {
                  tier: 'TIER_2A',
                  type: 'EMISSION_FACTOR',
                },
                {
                  tier: 'TIER_2',
                  type: 'ACTIVITY_DATA',
                },
                {
                  tier: 'NO_TIER',
                  type: 'NET_CALORIFIC_VALUE',
                },
                {
                  tier: 'TIER_1',
                  type: 'BIOMASS_FRACTION',
                },
              ],
              parameterCalculationMethod: {
                type: 'MANUAL',
                emissionCalculationParamValues: {
                  emissionFactor: '4',
                  oxidationFactor: '3',
                  efMeasurementUnit: 'TONNES_OF_CO2_PER_TJ',
                  netCalorificValue: '444',
                  providedEmissions: {
                    totalProvidedReportableEmissions: '100',
                    reasonForProvidingManualEmissions: 'r',
                    totalProvidedSustainableBiomassEmissions: '20',
                  },
                  calculationCorrect: false,
                  ncvMeasurementUnit: 'GJ_PER_NM3',
                  totalReportableEmissions: '865.26720',
                  totalSustainableBiomassEmissions: '370.82880',
                },
                calculationActivityDataCalculationMethod: {
                  type: 'CONTINUOUS_METERING',
                  totalMaterial: '232',
                  measurementUnit: 'NM3',
                },
              },
              parameterMonitoringTierDiffReason: {
                type: 'OTHER',
                reason: 'Velit nisi eum quia',
              },
            },
            {
              sourceStream: '16733400297060.8808995331842155',
              transfer: {
                entryAccountingForTransfer: false,
              },
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['16703224856040.6939830628582251'],
              biomassPercentages: {
                contains: true,
                biomassPercentage: '32',
                nonSustainableBiomassPercentage: '12',
              },
              parameterMonitoringTiers: [
                {
                  tier: 'NO_TIER',
                  type: 'OXIDATION_FACTOR',
                },
                {
                  tier: 'TIER_2',
                  type: 'EMISSION_FACTOR',
                },
                {
                  tier: 'NO_TIER',
                  type: 'ACTIVITY_DATA',
                },
                {
                  tier: 'TIER_2B',
                  type: 'NET_CALORIFIC_VALUE',
                },
                {
                  tier: 'TIER_3',
                  type: 'BIOMASS_FRACTION',
                },
              ],
              parameterCalculationMethod: {
                type: 'MANUAL',
                emissionCalculationParamValues: {
                  emissionFactor: '4',
                  oxidationFactor: '3',
                  efMeasurementUnit: 'TONNES_OF_CO2_PER_NM3',
                  netCalorificValue: '4',
                  providedEmissions: {
                    totalProvidedReportableEmissions: '300',
                    reasonForProvidingManualEmissions: 'sdfsd',
                    totalProvidedSustainableBiomassEmissions: '25',
                  },
                  calculationCorrect: false,
                  ncvMeasurementUnit: 'GJ_PER_NM3',
                  totalReportableEmissions: '40.80000',
                  totalSustainableBiomassEmissions: '19.20000',
                },
                calculationActivityDataCalculationMethod: {
                  type: 'CONTINUOUS_METERING',
                  totalMaterial: '5',
                  measurementUnit: 'NM3',
                },
              },
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'dfgdf',
              },
            },
            {
              sourceStream: '16733400442910.7831379162812164',
              transfer: {
                entryAccountingForTransfer: false,
              },
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['16703224856040.6939830628582251'],
              biomassPercentages: {
                contains: false,
              },
              parameterMonitoringTiers: [
                {
                  tier: 'NO_TIER',
                  type: 'ACTIVITY_DATA',
                },
                {
                  tier: 'TIER_1',
                  type: 'EMISSION_FACTOR',
                },
              ],
              parameterCalculationMethod: {
                type: 'MANUAL',
                emissionCalculationParamValues: {
                  emissionFactor: '2',
                  efMeasurementUnit: 'TONNES_OF_CO2_PER_TONNE',
                  calculationCorrect: true,
                  totalReportableEmissions: '464.00000',
                },
                calculationActivityDataCalculationMethod: {
                  type: 'CONTINUOUS_METERING',
                  totalMaterial: '232',
                  measurementUnit: 'TONNES',
                },
              },
              parameterMonitoringTierDiffReason: {
                type: 'OTHER',
                reason: 'Vitae unde nulla tem',
              },
            },
          ],
        },
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
              durationRange: {
                fullYearCovered: true,
              },
              tier: 'TIER_3',
              emissionPoint: '900',
              sourceStreams: ['16729074127320.4550922071395316'],
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
              annualGasFlow: '10',
            },
          ],
        },
        FALLBACK: {
          type: 'FALLBACK',
          sourceStreams: ['324', '325'],
          biomass: {
            contains: true,
            totalSustainableBiomassEmissions: '3.3',
            totalNonSustainableBiomassEmissions: '8.8',
            totalEnergyContentFromBiomass: '4.4',
          },
          description: 'Test description',
          files: ['ddc683ce-bc24-4ce5-8f5f-553fafc1baa8', 'e3c2ddba-053a-4486-a232-c6f983befc12'],
          totalFossilEmissions: '1.1',
          totalFossilEnergyContent: '2.2',
          reportableEmissions: '9.9',
        } as FallbackEmissions,
        CALCULATION_PFC: {
          type: 'CALCULATION_PFC',
          sourceStreamEmissions: [
            {
              id: '12345678-74c4-4da8-b59c-368f82d955fb',
              amountOfCF4: '0.204',
              amountOfC2F6: '27.744',
              sourceStream: '324',
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['853'],
              totalCF4Emissions: '1507.56',
              calculationCorrect: true,
              totalC2F6Emissions: '338476.8',
              reportableEmissions: '11332812.0',
              totalPrimaryAluminium: '34',
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
              } as any,
            },
          ],
        } as CalculationOfPfcEmissions,
        INHERENT_CO2: {
          type: 'INHERENT_CO2',
          inherentReceivingTransferringInstallations: [
            {
              id: '0479c5be-bfdd-4bbd-b5bf-b0509cfc62e9',
              inherentReceivingTransferringInstallation: {
                inherentCO2Direction: 'EXPORTED_TO_ETS_INSTALLATION',
                installationDetailsType: 'INSTALLATION_EMITTER',
                inherentReceivingTransferringInstallationDetailsType: {
                  installationEmitter: {
                    emitterId: 'EM12345',
                    email: '1@o.com',
                  },
                },
                measurementInstrumentOwnerTypes: ['INSTRUMENTS_BELONGING_TO_YOUR_INSTALLATION'],
                totalEmissions: '11.11111',
              },
            },
            {
              id: '680e7067-cd9f-404e-85d1-65e21ce30a15',
              inherentReceivingTransferringInstallation: {
                inherentCO2Direction: 'EXPORTED_TO_NON_ETS_CONSUMER',
                installationDetailsType: 'INSTALLATION_DETAILS',
                inherentReceivingTransferringInstallationDetailsType: {
                  installationDetails: {
                    installationName: 'Some installation',
                    line1: 'Egnatia street',
                    city: 'Thessaloniki',
                    postcode: '54555',
                    email: 'installation_exported@gmail.com',
                  },
                },
                measurementInstrumentOwnerTypes: ['INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION'],
                totalEmissions: '22.22222',
              },
            },
            {
              inherentReceivingTransferringInstallation: {
                inherentCO2Direction: 'RECEIVED_FROM_ANOTHER_INSTALLATION',
                installationDetailsType: 'INSTALLATION_EMITTER',
                inherentReceivingTransferringInstallationDetailsType: {
                  installationEmitter: {
                    emitterId: 'asdf',
                    email: '12@o.com',
                  },
                },
                measurementInstrumentOwnerTypes: ['INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION'],
                totalEmissions: '100',
              },
            },
          ],
        },
      },
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
      regulatedActivities: undefined,
      aerMonitoringPlanDeviation: undefined,
    } as Aer;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;

    component = fixture.debugElement.query(By.directive(EmissionsSummaryGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLTableRowElement>('govuk-table tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['Calculation of combustion', '300  t  (includes 39 t non-sustainable biomass)', '25  t'],
      ['Calculation of process', '464  t', '0  t'],
      ['Calculation of transferred CO2', '100  t  (includes 72 t non-sustainable biomass)', '20  t'],
      ['Measurement of CO2', '0  t', '0  t'],
      ['Measurement of transferred CO2', '-37.026  t', '0  t'],
      ['Calculation of PFC', '11332812  t', '0  t'],
      ['Fallback', '9.9  t  (includes 8.8 t non-sustainable biomass)', '3.3  t'],
      ['Total', '11333648.874  tCO2e', '48.3  tCO2e'],
      ['Inherent CO2', '66.66667  t', '0  t'],
    ]);
  });
});
