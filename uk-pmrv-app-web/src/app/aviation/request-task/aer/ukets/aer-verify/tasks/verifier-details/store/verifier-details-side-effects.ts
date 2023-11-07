import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/ukets/aer-verify/util/aer-verify.util';
import { AerVerifyTaskPayload } from '@aviation/request-task/store';
import { AerVerifySideEffectFn } from '@aviation/request-task/store/delegates/aer-verify/aer-verify-store-side-effects.handler';
import produce from 'immer';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

const sideEffectsToApply: AerVerifySideEffectFn<AviationAerUkEtsVerificationReport>[] = [
  applySideEffectsToVerifierDetails,
];

function applySideEffectsToVerifierDetails(
  payload: AerVerifyTaskPayload,
  update: AviationAerUkEtsVerificationReport,
): AerVerifyTaskPayload {
  const status = getTaskStatusByTaskCompletionState('verificationReport', payload);

  return ['in progress', 'complete'].includes(status)
    ? produce(payload, (updatedPayload) => {
        if (update) {
          handleDeleteUnnecessaryFields(updatedPayload);
        }
      })
    : payload;
}

function handleDeleteUnnecessaryFields(payload: AerVerifyTaskPayload) {
  delete payload.aer;
  delete payload.aerAttachments;
  delete payload.aerMonitoringPlanVersions;
  delete payload.aerSectionsCompleted;
  delete payload.etsComplianceRules;
  delete payload.overallDecision;
  delete payload.reportingObligationDetails;
  delete payload.reportingRequired;
  delete payload.reportingYear;
  delete payload.serviceContactDetails;
}

export function verifierDetailsSideEffects(
  payload: AerVerifyTaskPayload,
  update: AviationAerUkEtsVerificationReport,
): AerVerifyTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
