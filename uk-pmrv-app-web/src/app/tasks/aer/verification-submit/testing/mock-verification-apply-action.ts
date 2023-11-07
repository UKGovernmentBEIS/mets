import { mockAerApplyPayload, mockOnshore } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AerApplicationVerificationSubmitRequestTaskPayload,
  AerVerificationReport,
  NoSiteVisit,
  NotVerifiedOverallAssessment,
} from 'pmrv-api';

export const mockVerificationApplyPayload: AerApplicationVerificationSubmitRequestTaskPayload = {
  aerAttachments: mockAerApplyPayload.aerAttachments,
  payloadType: 'AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
  installationOperatorDetails: mockOnshore,
  monitoringPlanVersions: [],
  aer: mockAerApplyPayload.aer,
  verificationReport: {
    recommendedImprovements: {
      areThereRecommendedImprovements: false,
    },
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
    uncorrectedMisstatements: {
      areThereUncorrectedMisstatements: false,
    },
    uncorrectedNonCompliances: {
      areThereUncorrectedNonCompliances: false,
    },
    uncorrectedNonConformities: {
      areTherePriorYearIssues: false,
      areThereUncorrectedNonConformities: false,
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
    activityLevelReport: {
      freeAllocationOfAllowances: true,
      file: '11111111-1111-4111-a111-111111111111',
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
      regulatedActivities: [
        'COMBUSTION',
        'MINERAL_OIL_REFINING',
        'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION',
        'CARBON_BLACK_PRODUCTION',
        'COKE_PRODUCTION',
      ],
      combustionSources: ['Refinery fuel', 'Coal'],
      processSources: ['Calcination of lime', 'Waste gas scrubbing'],
      monitoringApproachDescription: 'Monitoring Approach Description',
      emissionFactorsDescription: 'Emission Factors Description',
      operatorEmissionsAcceptable: false,
      monitoringApproachTypeEmissions: {
        calculationCombustionEmissions: {
          reportableEmissions: '1.11111',
          sustainableBiomass: '11.11111',
        },
        calculationProcessEmissions: {
          reportableEmissions: '2.22222',
          sustainableBiomass: '22.22222',
        },
        calculationMassBalanceEmissions: {
          reportableEmissions: '3',
          sustainableBiomass: '33',
        },
        calculationTransferredCO2Emissions: {
          reportableEmissions: '4',
          sustainableBiomass: '44',
        },
        measurementCO2Emissions: {
          reportableEmissions: '5',
          sustainableBiomass: '55',
        },
        measurementTransferredCO2Emissions: {
          reportableEmissions: '6',
          sustainableBiomass: '66',
        },
        measurementN2OEmissions: {
          reportableEmissions: '7',
          sustainableBiomass: '77',
        },
        measurementTransferredN2OEmissions: {
          reportableEmissions: '8',
          sustainableBiomass: '88',
        },
        calculationPFCEmissions: {
          reportableEmissions: '9',
        },
        inherentCO2Emissions: {
          reportableEmissions: '10',
        },
        fallbackEmissions: {
          reportableEmissions: '11',
          sustainableBiomass: '1111',
        },
      },
      additionalChangesNotCovered: true,
      additionalChangesNotCoveredDetails: 'Some changes by the verifier',
      siteVisit: {
        siteVisitType: 'NO_VISIT',
        reason: 'reason',
      } as NoSiteVisit,
    },
    methodologiesToCloseDataGaps: {
      dataGapRequired: false,
    },
    overallAssessment: {
      type: 'NOT_VERIFIED',
      notVerifiedReasons: [
        {
          type: 'NOT_APPROVED_MONITORING_PLAN',
        },
      ],
    } as NotVerifiedOverallAssessment,
  } as AerVerificationReport,
  verificationAttachments: { '11111111-1111-4111-a111-111111111111': 'testfile1.pdf' },
  verificationSectionsCompleted: {
    verificationTeamDetails: [true],
    materialityLevel: [true],
    complianceMonitoringReporting: [true],
    etsComplianceRules: [true],
    opinionStatement: [true],
    methodologiesToCloseDataGaps: [true],
    overallAssessment: [true],
  },
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['AER_SAVE_APPLICATION_VERIFICATION'],
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
      type: 'AER_APPLICATION_VERIFICATION_SUBMIT',
      payload: mockVerificationApplyPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
