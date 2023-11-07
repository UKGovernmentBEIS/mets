import { mockOnshore } from '@tasks/aer/submit/testing/mock-aer-apply-action';
import { mockVerificationApplyPayload } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AerApplicationReviewRequestTaskPayload,
  AerDataReviewDecision,
  AerVerificationReportDataReviewDecision,
} from 'pmrv-api';

const mockAerDataReviewDecision = {
  reviewDataType: 'AER_DATA',
  type: 'ACCEPTED',
  details: {
    notes: 'Notes',
  },
} as AerDataReviewDecision;

export const mockAerDataReviewDecisionAmends = {
  reviewDataType: 'AER_DATA',
  type: 'OPERATOR_AMENDS_NEEDED',
  details: {
    requiredChanges: [
      {
        reason: 'Reason',
      },
    ],
    notes: 'Notes',
  },
} as AerDataReviewDecision;

const mockVerificationDataReviewDecision = {
  reviewDataType: 'VERIFICATION_REPORT_DATA',
  type: 'ACCEPTED',
  details: {
    notes: 'Notes',
  },
} as AerVerificationReportDataReviewDecision;

export const mockReview: AerApplicationReviewRequestTaskPayload = {
  aerAttachments: mockVerificationApplyPayload.aerAttachments,
  payloadType: 'AER_APPLICATION_REVIEW_PAYLOAD',
  installationOperatorDetails: mockOnshore,
  monitoringPlanVersions: [],
  aer: mockVerificationApplyPayload.aer,
  verificationReport: mockVerificationApplyPayload.verificationReport,
  reviewGroupDecisions: {
    INSTALLATION_DETAILS: mockAerDataReviewDecision,
    FUELS_AND_EQUIPMENT: mockAerDataReviewDecision,
    ADDITIONAL_INFORMATION: mockAerDataReviewDecision,
    ACTIVITY_LEVEL_REPORT: mockAerDataReviewDecision,
    EMISSIONS_SUMMARY: mockAerDataReviewDecision,
    CALCULATION_CO2: mockAerDataReviewDecision,
    MEASUREMENT_CO2: mockAerDataReviewDecision,
    FALLBACK: mockAerDataReviewDecision,
    MEASUREMENT_N2O: mockAerDataReviewDecision,
    CALCULATION_PFC: mockAerDataReviewDecision,
    INHERENT_CO2: mockAerDataReviewDecision,
    VERIFIER_DETAILS: mockVerificationDataReviewDecision,
    OPINION_STATEMENT: mockVerificationDataReviewDecision,
    ETS_COMPLIANCE_RULES: mockVerificationDataReviewDecision,
    COMPLIANCE_MONITORING_REPORTING: mockVerificationDataReviewDecision,
    OVERALL_DECISION: mockVerificationDataReviewDecision,
    UNCORRECTED_MISSTATEMENTS: mockVerificationDataReviewDecision,
    UNCORRECTED_NON_CONFORMITIES: mockVerificationDataReviewDecision,
    UNCORRECTED_NON_COMPLIANCES: mockVerificationDataReviewDecision,
    RECOMMENDED_IMPROVEMENTS: mockVerificationDataReviewDecision,
    CLOSE_DATA_GAPS_METHODOLOGIES: mockVerificationDataReviewDecision,
    MATERIALITY_LEVEL: mockVerificationDataReviewDecision,
    SUMMARY_OF_CONDITIONS: mockVerificationDataReviewDecision,
  },
  reviewSectionsCompleted: {
    INSTALLATION_DETAILS: true,
    FUELS_AND_EQUIPMENT: true,
    ADDITIONAL_INFORMATION: true,
    ACTIVITY_LEVEL_REPORT: true,
    EMISSIONS_SUMMARY: true,
    CALCULATION_CO2: true,
    MEASUREMENT_CO2: true,
    FALLBACK: true,
    MEASUREMENT_N2O: true,
    CALCULATION_PFC: true,
    INHERENT_CO2: true,
    VERIFIER_DETAILS: true,
    OPINION_STATEMENT: true,
    ETS_COMPLIANCE_RULES: true,
    COMPLIANCE_MONITORING_REPORTING: true,
    OVERALL_DECISION: true,
    UNCORRECTED_MISSTATEMENTS: true,
    UNCORRECTED_NON_CONFORMITIES: true,
    UNCORRECTED_NON_COMPLIANCES: true,
    RECOMMENDED_IMPROVEMENTS: true,
    CLOSE_DATA_GAPS_METHODOLOGIES: true,
    MATERIALITY_LEVEL: true,
    SUMMARY_OF_CONDITIONS: true,
  },
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['AER_SAVE_REVIEW_GROUP_DECISION', 'AER_COMPLETE_REVIEW'],
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
      type: 'AER_APPLICATION_REVIEW',
      payload: mockReview,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
