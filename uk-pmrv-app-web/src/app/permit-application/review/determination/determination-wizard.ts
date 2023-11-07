import { PermitApplicationState } from '../../store/permit-application.state';

export function isHSEAnnualEmissionTargetsCompleted(state: PermitApplicationState) {
  return (
    !!state?.determination?.annualEmissionsTargets && Object.keys(state.determination.annualEmissionsTargets).length > 0
  );
}

// export function resolveCaption(state: PermitApplicationState) {
//   return !isDeterminationTypeApplicable(state) ? 'GRANTED' : (state.determination?.type as string);
// }

// export function isDeterminationTypeApplicable(state: PermitApplicationState) {
//   return !isVariationRegulatorLedRequest(state);
// }

// export function isWizardComplete(state: PermitApplicationState): boolean {
//   if (state.determination?.type === 'GRANTED' || !isDeterminationTypeApplicable(state)) {
//     return isGrantWizardComplete(state);
//   } else if (state.determination?.type === 'REJECTED') {
//     return isRejectWizardComplete(state.determination);
//   } else if (state.determination?.type === 'DEEMED_WITHDRAWN') {
//     return isDeemWithdrawnWizardComplete(state.determination);
//   }
//   return false;
// }

// export function isVariationReasonTemplateCompleted(determination: PermitVariationRegulatorLedGrantDetermination) {
//   return (
//     !!determination?.reasonTemplate &&
//     (determination.reasonTemplate !== 'OTHER' || !!determination.reasonTemplateOtherSummary)
//   );
// }

// export function isHSEAnnualEmissionTargetsCompleted(state: PermitApplicationState) {
//   return (
//     !!state?.determination?.annualEmissionsTargets && Object.keys(state.determination.annualEmissionsTargets).length > 0
//   );
// }

// function isGrantWizardComplete(state: PermitApplicationState): boolean {
//   return (
//     !!state?.determination?.reason &&
//     !!state?.determination?.activationDate &&
//     (state.permitType === 'GHGE' || isHSEAnnualEmissionTargetsCompleted(state)) &&
//     (!getVariationRequestTaskTypes().includes(state.requestTaskType) || !!state?.determination?.logChanges) &&
//     (!isVariationRegulatorLedRequestTask(state.requestTaskType) ||
//       isVariationReasonTemplateCompleted(state.determination))
//   );
// }

// function isRejectWizardComplete(
//   rejectDetermination: PermitIssuanceRejectDetermination | PermitVariationRejectDetermination,
// ): boolean {
//   return !!rejectDetermination.reason && !!rejectDetermination.officialNotice;
// }

// function isDeemWithdrawnWizardComplete(
//   deemWithdrawnDetermination: PermitIssuanceDeemedWithdrawnDetermination | PermitVariationDeemedWithdrawnDetermination,
// ): boolean {
//   return !!deemWithdrawnDetermination.reason;
// }
