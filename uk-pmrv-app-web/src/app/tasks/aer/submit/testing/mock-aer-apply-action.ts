import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationOfPfcEmissions,
  FallbackEmissions,
  InherentCO2Emissions,
  InstallationOperatorDetails,
  MeasurementOfCO2Emissions,
} from 'pmrv-api';

export const mockOnshore = {
  companyReferenceNumber: '88888',
  installationLocation: {
    type: 'ONSHORE',
    address: {
      line1: 'Korinthou 4, Neo Psychiko',
      line2: 'line 2 legal test',
      city: 'Athens',
      country: 'GR',
      postcode: '15452',
    },
    gridReference: 'NN166712',
  },
  installationName: 'onshore permit installation',
  operator: 'onshore permit',
  operatorDetailsAddress: {
    line1: 'Korinthou 3, Neo Psychiko',
    line2: 'line 2 legal test',
    city: 'Athens',
    country: 'GR',
    postcode: '15451',
  },
  operatorType: 'LIMITED_COMPANY',
  siteName: 'site name',
} as InstallationOperatorDetails;

export const mockAerApplyPayload: AerApplicationSubmitRequestTaskPayload = {
  aerSectionsCompleted: {
    aerMonitoringPlanDeviation: [true],
    abbreviations: [true],
    additionalDocuments: [true],
    confidentialityStatement: [true],
    activityLevelReport: [true],
    pollutantRegisterActivities: [true],
    sourceStreams: [false],
    monitoringApproachEmissions: [true],
    emissionSources: [false],
    naceCodes: [true],
    emissionPoints: [false],
    CALCULATION_CO2: [true],
    CALCULATION_PFC: [true],
    MEASUREMENT_CO2: [true],
    INHERENT_CO2: [true],
    FALLBACK: [true],
  },
  aerAttachments: {
    '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'cover.jpg',
    '60fe9548-ac65-492a-b057-60033b0fbbea': 'PublicationAgreement.pdf',
    '11111111-1111-4111-a111-111111111111': 'testfile1.pdf',
  },
  payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
  installationOperatorDetails: mockOnshore,
  permitType: 'GHGE',
  monitoringPlanVersions: [],
  aer: {
    naceCodes: {
      codes: ['_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT', '_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT'],
    },
    abbreviations: {
      exist: true,
      abbreviationDefinitions: [
        {
          abbreviation: 'Mr',
          definition: 'Mister',
        },
        {
          abbreviation: 'Ms',
          definition: 'Miss',
        },
      ],
    },
    additionalDocuments: {
      exist: true,
      documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd', '60fe9548-ac65-492a-b057-60033b0fbbea'],
    },
    confidentialityStatement: {
      exist: true,
      confidentialSections: [
        {
          section: 'Section 1',
          explanation: 'Explanation 1',
        },
        {
          section: 'Section 2',
          explanation: 'Explanation 2',
        },
      ],
    },
    activityLevelReport: {
      freeAllocationOfAllowances: true,
      file: '11111111-1111-4111-a111-111111111111',
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
    pollutantRegisterActivities: {
      exist: false,
    },
    monitoringApproachEmissions: {
      CALCULATION_CO2: {
        type: 'CALCULATION_CO2',
        hasTransfer: true,
        sourceStreamEmissions: [
          {
            sourceStream: '324',
            transfer: {
              transferDirection: 'EXPORTED_TO_LONG_TERM_FACILITY',
              installationEmitter: {
                email: 'permitsubmit1@trasys.gr',
                emitterId: '34',
              },
              entryAccountingForTransfer: true,
              installationDetailsType: 'INSTALLATION_EMITTER',
              transferType: 'TRANSFER_CO2',
            },
            durationRange: {
              fullYearCovered: true,
            },
            emissionSources: ['853'],
            biomassPercentages: {
              contains: false,
            },
            parameterMonitoringTiers: [
              {
                tier: 'NO_TIER',
                type: 'EMISSION_FACTOR',
              },
              {
                tier: 'NO_TIER',
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
            parameterCalculationMethod: {
              type: 'MANUAL',
              emissionCalculationParamValues: {
                emissionFactor: '3',
                oxidationFactor: '3',
                efMeasurementUnit: 'TONNES_OF_CO2_PER_NM3',
                netCalorificValue: '4',
                calculationCorrect: true,
                ncvMeasurementUnit: 'GJ_PER_NM3',
                totalReportableEmissions: '18',
              },
              calculationActivityDataCalculationMethod: {
                type: 'CONTINUOUS_METERING',
                totalMaterial: '2',
                measurementUnit: 'NM3',
              },
            } as any,
            parameterMonitoringTierDiffReason: {
              type: 'DATA_GAP',
              reason: 'reason',
            },
          },
        ],
      } as CalculationOfCO2Emissions,
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
            annualGasFlow: '8',
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
              totalEmissions: '3',
            },
          },
          {
            id: '680e7067-cd9f-404e-85d1-65e21ce30a15',
            inherentReceivingTransferringInstallation: {
              inherentCO2Direction: 'EXPORTED_TO_NON_ETS_CONSUMER',
              installationDetailsType: 'INSTALLATION_DETAILS',
              inherentReceivingTransferringInstallationDetailsType: {
                installationDetails: {
                  installationName: 'Test installation',
                  line1: 'Test street',
                  city: 'Berlin',
                  postcode: '54555',
                  email: '1@o.com',
                },
              },
              measurementInstrumentOwnerTypes: ['INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION'],
              totalEmissions: '1',
            },
          },
        ],
      } as InherentCO2Emissions,
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
    },
    regulatedActivities: [
      {
        id: '324',
        type: 'AMMONIA_PRODUCTION',
        capacity: '100',
        capacityUnit: 'KVA',
        hasEnergyCrf: true,
        hasIndustrialCrf: true,
        energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
        industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
      },
    ],
    aerMonitoringPlanDeviation: {
      existChangesNotCoveredInApprovedVariations: true,
      details: 'the details of the deviation',
    },
  },
  permitOriginatedData: {
    permitType: 'GHGE',
    installationCategory: 'A_LOW_EMITTER',
    permitMonitoringApproachMonitoringTiers: {
      measurementCO2EmissionPointParamMonitoringTiers: {
        'f61d8b13-74c4-4da8-b59c-368f82d955fb': 'TIER_3',
      },
      calculationPfcSourceStreamParamMonitoringTiers: {
        '12345678-74c4-4da8-b59c-368f82d955fb': {
          activityDataTier: 'TIER_1',
          emissionFactorTier: 'TIER_2',
          massBalanceApproachUsed: false,
        },
      },
    },
  },
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['AER_SAVE_APPLICATION', 'AER_UPLOAD_SECTION_ATTACHMENT'],
    requestInfo: {
      id: 'AEM210-2021',
      type: 'AER',
      competentAuthority: 'WALES',
      accountId: 210,
      requestMetadata: {
        type: 'AER',
        year: '2021',
      },
    },
    requestTask: {
      id: 1,
      type: 'AER_APPLICATION_SUBMIT',
      payload: mockAerApplyPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
