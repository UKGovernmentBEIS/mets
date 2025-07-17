import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer.utils';
import produce from 'immer';

import { ReportingObligation } from '../reporting-obligation.interface';

const sideEffectsToApply: AerSideEffectFn<ReportingObligation>[] = [applySideEffectsToReportingObligation];

function applySideEffectsToReportingObligation(
  payload: AerRequestTaskPayload,
  update: ReportingObligation,
): AerRequestTaskPayload {
  return produce(payload, (updatedPayload) => {
    update.reportingRequired === false
      ? handleReportingNotRequired(updatedPayload)
      : handleReportingRequired(updatedPayload);
  });
}

function handleReportingNotRequired(payload: AerRequestTaskPayload) {
  payload.aer = undefined;
  payload.aerMonitoringPlanVersions = [];

  payload.aerSectionsCompleted = {
    reportingObligation: [false],
    ...(payload.aerSectionsCompleted['changesRequested'] && {
      changesRequested: payload.aerSectionsCompleted['changesRequested'],
    }),
  };
}

function handleReportingRequired(payload: AerRequestTaskPayload) {
  if (!payload.aer) {
    payload.aer = { operatorDetails: payload.empOriginatedData?.operatorDetails } as any;
    payload.aerAttachments = payload.empOriginatedData?.operatorDetailsAttachments;
  }
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
