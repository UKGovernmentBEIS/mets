import { AerDataReviewDecision, AerVerificationReportDataReviewDecision } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';
import { mockStateReviewed } from '../../submitted/testing/mock-aer-submitted';

const mockAerDataReviewDecision = {
  reviewDataType: 'AER_DATA',
  type: 'ACCEPTED',
  details: {
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

export const mockStateCompleted = {
  storeInitialized: true,
  action: {
    ...mockStateReviewed,
    type: 'AER_APPLICATION_COMPLETED',
    payload: {
      ...mockStateReviewed.action.payload,
      payloadType: 'AER_APPLICATION_COMPLETED_PAYLOAD',
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
        VERIFICATION_ACTIVITY_LEVEL_REPORT: mockAerDataReviewDecision,
        OVERALL_DECISION: mockVerificationDataReviewDecision,
        UNCORRECTED_MISSTATEMENTS: mockVerificationDataReviewDecision,
        UNCORRECTED_NON_CONFORMITIES: mockVerificationDataReviewDecision,
        UNCORRECTED_NON_COMPLIANCES: mockVerificationDataReviewDecision,
        RECOMMENDED_IMPROVEMENTS: mockVerificationDataReviewDecision,
        CLOSE_DATA_GAPS_METHODOLOGIES: mockVerificationDataReviewDecision,
        MATERIALITY_LEVEL: mockVerificationDataReviewDecision,
        SUMMARY_OF_CONDITIONS: mockVerificationDataReviewDecision,
      },
    },
  },
} as CommonActionsState;
