import {
  ALRNotVerifiedOverallVerificationAssessment,
  ALRVerificationReport,
  ALRVerifiedSatisfactoryOverallVerificationAssessment,
  ALRVerifiedWithCommentsOverallVerificationAssessment,
  BDRNotVerifiedOverallVerificationAssessment,
  BDRVerificationReport,
  BDRVerifiedSatisfactoryOverallVerificationAssessment,
  BDRVerifiedWithCommentsOverallVerificationAssessment,
} from 'pmrv-api';

export type OverallVerificationAssessment = BDRVerifiedSatisfactoryOverallVerificationAssessment &
  BDRVerifiedWithCommentsOverallVerificationAssessment &
  BDRNotVerifiedOverallVerificationAssessment &
  ALRVerifiedSatisfactoryOverallVerificationAssessment &
  ALRVerifiedWithCommentsOverallVerificationAssessment &
  ALRNotVerifiedOverallVerificationAssessment;

export const overallDecisionWizardCompleted = (
  overallAssessment: (BDRVerificationReport | ALRVerificationReport)['overallAssessment'],
) => {
  switch (overallAssessment?.type) {
    case 'VERIFIED_AS_SATISFACTORY':
      return true;
    case 'VERIFIED_WITH_COMMENTS':
      return !!(
        overallAssessment as BDRVerifiedWithCommentsOverallVerificationAssessment &
          ALRVerifiedWithCommentsOverallVerificationAssessment
      )?.reasons;
    case 'NOT_VERIFIED':
      return !!(
        overallAssessment as BDRNotVerifiedOverallVerificationAssessment & ALRNotVerifiedOverallVerificationAssessment
      )?.reasons;

    default:
      return false;
  }
};
