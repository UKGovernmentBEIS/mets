import {
  CalculationOfCO2Emissions,
  FallbackEmissions,
  InherentCO2Emissions,
  MeasurementOfCO2Emissions,
  NoSiteVisit,
  NotVerifiedOverallAssessment,
} from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockState = {
  storeInitialized: true,
  action: {
    id: 1,
    requestAccountId: 13,
    submitter: 'Operator',
    creationDate: '2022-11-29T12:12:48.469862Z',
    type: 'AER_APPLICATION_SUBMITTED',
    payload: {
      payloadType: 'AER_APPLICATION_SUBMITTED_PAYLOAD',
      aer: {
        naceCodes: {
          codes: ['_1011_PROCESSING_AND_PRESERVING_OF_MEAT', '_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT'],
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
          exist: false,
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
            sourceStreamEmissions: [
              {
                sourceStream: '324',
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
                    emissionFactor: 3,
                    oxidationFactor: 3,
                    efMeasurementUnit: 'TONNES_OF_CO2_PER_NM3',
                    netCalorificValue: 4,
                    calculationCorrect: true,
                    ncvMeasurementUnit: 'GJ_PER_NM3',
                    totalReportableEmissions: 18,
                  },
                  calculationActivityDataCalculationMethod: {
                    type: 'CONTINUOUS_METERING',
                    totalMaterial: 2,
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
                annualGasFlow: '10',
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
        regulatedActivities: [
          {
            id: '324',
            type: 'AMMONIA_PRODUCTION',
            capacity: 100,
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
        activityLevelReport: {
          freeAllocationOfAllowances: true,
          file: '11111111-1111-4111-a111-111111111111',
        },
      },
      installationOperatorDetails: {
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
      },
      monitoringPlanVersions: [
        {
          endDate: '2022-12-14',
          permitId: 'UK-W-IN-00013',
          permitConsolidationNumber: 1,
        },
      ],
      aerAttachments: {},
    },
  },
} as CommonActionsState;

export const mockStateReviewed = {
  storeInitialized: true,
  action: {
    id: 1,
    requestAccountId: 13,
    submitter: 'Regulator',
    creationDate: '2022-11-29T12:12:48.469862Z',
    type: 'AER_APPLICATION_COMPLETED',
    payload: {
      ...mockState.action.payload,
      payloadType: 'AER_APPLICATION_COMPLETED_PAYLOAD',
      verificationReport: {
        verificationBodyDetails: {
          name: 'My Verification Body',
          accreditationReferenceNumber: '6789',
          address: {
            line1: 'line',
            line2: null,
            city: 'town',
            country: 'GR',
            postcode: '1231',
          },
          emissionTradingSchemes: ['UK_ETS_INSTALLATIONS', 'EU_ETS_INSTALLATIONS'],
        },
        verificationTeamDetails: {
          leadEtsAuditor: 'Lead ETS Auditor',
          etsAuditors: 'ETS Auditors',
          etsTechnicalExperts: 'ETS Experts',
          independentReviewer: 'reviewer',
          technicalExperts: 'Reviewer Experts',
          authorisedSignatoryName: 'Authorised signatory',
        },
        verifierContact: {
          name: 'VerifierAdminFirst VerifierAdminLast',
          email: 'verifieradmin@xx.gr',
          phoneNumber: '6995286257',
        },
        materialityLevel: {
          accreditationReferenceDocumentTypes: ['EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_EA_6_03'],
          materialityDetails: 'Materiality details',
        },
        complianceMonitoringReporting: {
          accuracy: true,
          completeness: true,
          consistency: true,
          comparability: true,
          transparency: true,
          integrity: true,
        },
        etsComplianceRules: {
          monitoringPlanRequirementsMet: true,
          euRegulationMonitoringReportingMet: true,
          detailSourceDataVerified: true,
          partOfSiteVerification: 'Yes detail source data reason',
          controlActivitiesDocumented: true,
          proceduresMonitoringPlanDocumented: true,
          dataVerificationCompleted: true,
          monitoringApproachAppliedCorrectly: true,
          plannedActualChangesReported: true,
          methodsApplyingMissingDataUsed: true,
          uncertaintyAssessment: true,
          competentAuthorityGuidanceMet: true,
          nonConformities: 'YES',
        },
        summaryOfConditions: {
          changesNotIncludedInPermit: true,
          approvedChangesNotIncluded: [{ reference: 'A1', explanation: 'Explanation A1' }],
          changesIdentified: true,
          notReportedChanges: [{ reference: 'B1', explanation: 'Explanation B1' }],
        },
        opinionStatement: {
          siteVisit: {
            siteVisitType: 'NO_VISIT',
            reason: 'reason',
          } as NoSiteVisit,
          combustionSources: ['Coal'],
          processSourcesUsed: false,
          regulatedActivities: ['COMBUSTION'],
          combustionSourcesUsed: true,
          emissionFactorsDescription: 'Emission Factors Description',
          additionalChangesNotCovered: false,
          operatorEmissionsAcceptable: true,
          monitoringApproachDescription: 'Monitoring Approach Description',
        },
        methodologiesToCloseDataGaps: {
          dataGapRequired: false,
        },
        uncorrectedMisstatements: {
          areThereUncorrectedMisstatements: false,
        },
        activityLevelReport: {
          freeAllocationOfAllowances: true,
          file: '22222222-2222-4222-a222-222222222222',
        },
        verificationAttachments: {
          '22222222-2222-4222-a222-222222222222': '300.png',
        },
        overallAssessment: {
          type: 'NOT_VERIFIED',
          notVerifiedReasons: [
            {
              type: 'NOT_APPROVED_MONITORING_PLAN',
            },
          ],
        } as NotVerifiedOverallAssessment,
      },
    },
  },
} as CommonActionsState;
