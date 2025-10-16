import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

export function isWizardComplete(
  determination: PermitSurrenderReviewDeterminationGrant,
  isFinalAlrVisible?: boolean,
): boolean {
  return (
    determination !== undefined &&
    !!determination?.type &&
    !!determination?.reason &&
    !!determination?.stopDate &&
    !!determination?.noticeDate &&
    ((determination?.reportRequired === true && !!determination?.reportDate) ||
      determination?.reportRequired === false) &&
    (isFinalAlrVisible
      ? (determination?.alrRequired === true && !!determination?.alrReportDate) || determination?.alrRequired === false
      : true) &&
    ((determination?.allowancesSurrenderRequired === true && !!determination?.allowancesSurrenderDate) ||
      determination?.allowancesSurrenderRequired === false)
  );
}
