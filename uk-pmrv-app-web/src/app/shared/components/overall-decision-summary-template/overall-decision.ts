import {
  ALRNotVerifiedOverallVerificationAssessment,
  ALRVerificationReport,
  ALRVerifiedSatisfactoryOverallVerificationAssessment,
  ALRVerifiedWithCommentsOverallVerificationAssessment,
  BDRNotVerifiedOverallVerificationAssessment,
  BDRS2NotVerifiedOverallVerificationAssessment,
  BDRS2VerificationReport,
  BDRS2VerifiedSatisfactoryOverallVerificationAssessment,
  BDRS2VerifiedWithCommentsOverallVerificationAssessment,
  BDRVerificationReport,
  BDRVerifiedSatisfactoryOverallVerificationAssessment,
  BDRVerifiedWithCommentsOverallVerificationAssessment,
  NERVerifiedSatisfactoryOverallVerificationAssessment,
  NERVerifiedWithCommentsOverallVerificationAssessment,
} from 'pmrv-api';

export type OverallVerificationAssessment = BDRVerifiedSatisfactoryOverallVerificationAssessment &
  BDRVerifiedWithCommentsOverallVerificationAssessment &
  BDRNotVerifiedOverallVerificationAssessment &
  ALRVerifiedSatisfactoryOverallVerificationAssessment &
  ALRVerifiedWithCommentsOverallVerificationAssessment &
  ALRNotVerifiedOverallVerificationAssessment &
  BDRS2VerifiedSatisfactoryOverallVerificationAssessment &
  BDRS2VerifiedWithCommentsOverallVerificationAssessment &
  BDRS2NotVerifiedOverallVerificationAssessment &
  NERVerifiedSatisfactoryOverallVerificationAssessment &
  NERVerifiedWithCommentsOverallVerificationAssessment;

export const overallDecisionWizardCompleted = (
  overallAssessment: (BDRVerificationReport | ALRVerificationReport | BDRS2VerificationReport)['overallAssessment'],
) => {
  switch (overallAssessment?.type) {
    case 'VERIFIED_AS_SATISFACTORY':
      return true;
    case 'VERIFIED_WITH_COMMENTS':
      return !!(
        overallAssessment as BDRVerifiedWithCommentsOverallVerificationAssessment &
          ALRVerifiedWithCommentsOverallVerificationAssessment &
          BDRS2VerifiedWithCommentsOverallVerificationAssessment
      )?.reasons;
    case 'NOT_VERIFIED':
      return !!(
        overallAssessment as BDRNotVerifiedOverallVerificationAssessment &
          ALRNotVerifiedOverallVerificationAssessment &
          BDRS2NotVerifiedOverallVerificationAssessment
      )?.reasons;

    default:
      return false;
  }
};
