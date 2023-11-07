import { SummaryOfConditions } from 'pmrv-api';

export function summaryOfConditionsWizardComplete(summaryOfConditionsInfo: SummaryOfConditions) {
  return (
    summaryOfConditionsInfo &&
    summaryOfConditionsInfo.changesNotIncludedInPermit !== undefined &&
    summaryOfConditionsInfo.changesIdentified !== undefined &&
    (!summaryOfConditionsInfo.changesNotIncludedInPermit ||
      (summaryOfConditionsInfo.changesNotIncludedInPermit &&
        summaryOfConditionsInfo.approvedChangesNotIncluded?.length > 0)) &&
    (!summaryOfConditionsInfo.changesIdentified ||
      (summaryOfConditionsInfo.changesIdentified && summaryOfConditionsInfo.notReportedChanges?.length > 0))
  );
}
