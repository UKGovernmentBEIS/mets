import { NotVerifiedOverallAssessment, OverallAssessment, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

export function overallDecisionWizardComplete(overallAssessmentInfo: OverallAssessment): boolean {
  return !!overallAssessmentInfo && !!overallAssessmentInfo.type && isAssessmentComplete(overallAssessmentInfo);
}

function isAssessmentComplete(overallAssessmentInfo: OverallAssessment): boolean {
  switch (overallAssessmentInfo.type) {
    case 'VERIFIED_AS_SATISFACTORY':
      return true;
    case 'VERIFIED_WITH_COMMENTS':
      return isVerifiedWithCommentsComplete(overallAssessmentInfo);
    case 'NOT_VERIFIED':
      return isNotVerifiedComplete(overallAssessmentInfo);
    default:
      return false;
  }
}

function isVerifiedWithCommentsComplete(overallAssessmentInfo: OverallAssessment): boolean {
  const verifiedWithComments = overallAssessmentInfo as VerifiedWithCommentsOverallAssessment;
  return verifiedWithComments?.reasons?.length > 0;
}

function isNotVerifiedComplete(overallAssessmentInfo: OverallAssessment): boolean {
  const notVerified = overallAssessmentInfo as NotVerifiedOverallAssessment;
  return notVerified?.notVerifiedReasons?.length > 0;
}
