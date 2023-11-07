import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export function isWizardCompleted(nonCompliance: NonComplianceApplicationSubmitRequestTaskPayload) {
  return (
    nonCompliance !== undefined &&
    isDetailsOfBreachStepCompleted(nonCompliance) &&
    isCivilPenaltyStepCompleted(nonCompliance) &&
    isNoticeOfIntentStepCompleted(nonCompliance) &&
    isDailyPenaltyStepCompleted(nonCompliance)
  );
}

export function isDetailsOfBreachStepCompleted(nonCompliance: NonComplianceApplicationSubmitRequestTaskPayload) {
  return nonCompliance !== undefined && nonCompliance.reason !== undefined;
}

export function isCivilPenaltyStepCompleted(nonCompliance: NonComplianceApplicationSubmitRequestTaskPayload) {
  return (
    nonCompliance !== undefined &&
    nonCompliance.civilPenalty !== undefined &&
    (nonCompliance.civilPenalty === true ||
      (nonCompliance.civilPenalty === false && nonCompliance.noCivilPenaltyJustification !== ''))
  );
}

export function isNoticeOfIntentStepCompleted(nonCompliance: NonComplianceApplicationSubmitRequestTaskPayload) {
  return (
    nonCompliance !== undefined &&
    ((nonCompliance.civilPenalty === false && nonCompliance.noCivilPenaltyJustification !== '') ||
      (nonCompliance.civilPenalty === true && nonCompliance.noticeOfIntent !== undefined))
  );
}

export function isDailyPenaltyStepCompleted(nonCompliance: NonComplianceApplicationSubmitRequestTaskPayload) {
  return (
    nonCompliance !== undefined &&
    ((nonCompliance.civilPenalty === false && nonCompliance.noCivilPenaltyJustification !== '') ||
      (nonCompliance.civilPenalty === true && nonCompliance.dailyPenalty !== undefined))
  );
}
