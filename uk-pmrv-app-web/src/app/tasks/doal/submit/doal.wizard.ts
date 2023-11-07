import {
  DoalAdditionalDocuments,
  DoalClosedDetermination,
  DoalDetermination,
  DoalProceedToAuthorityDetermination,
  OperatorActivityLevelReport,
  VerificationReportOfTheActivityLevelReport,
} from 'pmrv-api';

export function isOperatorActivityLevelReportPopulated(operatorActivityLevelReport: OperatorActivityLevelReport) {
  return (
    operatorActivityLevelReport !== undefined &&
    !!operatorActivityLevelReport.document &&
    operatorActivityLevelReport.areActivityLevelsEstimated !== undefined &&
    !!operatorActivityLevelReport.comment
  );
}

export function isVerificationActivityLevelReportPopulated(
  verificationReportOfTheActivityLevelReport: VerificationReportOfTheActivityLevelReport,
) {
  return (
    verificationReportOfTheActivityLevelReport !== undefined &&
    !!verificationReportOfTheActivityLevelReport.document &&
    !!verificationReportOfTheActivityLevelReport.comment
  );
}

export function isAdditionalDocumentsPopulated(additionalDocuments: DoalAdditionalDocuments) {
  return (
    additionalDocuments !== undefined &&
    additionalDocuments.exist !== undefined &&
    (additionalDocuments.exist === false || additionalDocuments.documents?.length > 0)
  );
}

export function isDeterminationPopulated(determination: DoalDetermination) {
  return (
    determination !== undefined &&
    !!determination.type &&
    ((determination.type === 'CLOSED' &&
      isCloseDeterminationPopulated(determination as DoalProceedToAuthorityDetermination)) ||
      (determination.type === 'PROCEED_TO_AUTHORITY' &&
        isProceedAuthorityDeterminationPopulated(determination as DoalProceedToAuthorityDetermination)))
  );
}

function isCloseDeterminationPopulated(determination: DoalClosedDetermination) {
  return !!(determination as DoalClosedDetermination)?.reason;
}

function isProceedAuthorityDeterminationPopulated(determination: DoalProceedToAuthorityDetermination) {
  return (
    !!determination.articleReasonGroupType &&
    !!determination.articleReasonItems?.length &&
    !!determination.reason &&
    determination.hasWithholdingOfAllowances !== undefined &&
    determination.hasWithholdingOfAllowances !== null &&
    (determination.hasWithholdingOfAllowances === false ||
      !!determination.withholdingAllowancesNotice?.withholdingOfAllowancesComment) &&
    determination.needsOfficialNotice !== undefined &&
    determination.needsOfficialNotice !== null
  );
}
