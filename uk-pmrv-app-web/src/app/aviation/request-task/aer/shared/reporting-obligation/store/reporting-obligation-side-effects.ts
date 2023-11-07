import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer-store-side-effects.handler';
import produce from 'immer';

import { ReportingObligation } from '../reporting-obligation.interface';

const sideEffectsToApply: AerSideEffectFn<ReportingObligation>[] = [applySideEffectsToReportingObligation];

function applySideEffectsToReportingObligation(
  payload: AerRequestTaskPayload,
  update: ReportingObligation,
): AerRequestTaskPayload {
  const status = getTaskStatusByTaskCompletionState('reportingObligation', payload);

  return ['in progress', 'complete'].includes(status)
    ? produce(payload, (updatedPayload) => {
        if (update.reportingRequired === false) {
          handleReportingNotRequired(updatedPayload);
        } else {
          handleReportingRequired(updatedPayload);
        }
      })
    : payload;
}

function handleReportingNotRequired(payload: AerRequestTaskPayload) {
  delete payload.aer;
  delete payload.aerAttachments;
  delete payload.aerMonitoringPlanVersions;

  if (payload.aerSectionsCompleted['changesRequested']) {
    payload.aerSectionsCompleted = {
      reportingObligation: [false],
      changesRequested: payload.aerSectionsCompleted['changesRequested'],
    };
  } else {
    payload.aerSectionsCompleted = {
      reportingObligation: [false],
    };
  }
}

function handleReportingRequired(payload: AerRequestTaskPayload) {
  payload.aer = {
    ...payload.aer,
    operatorDetails: payload.aer?.operatorDetails ?? payload?.empOriginatedData?.operatorDetails,
  } as any;
}

export function reportingObligationSideEffects(
  payload: AerRequestTaskPayload,
  update: ReportingObligation,
): AerRequestTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
