import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export function submitReviewWizardComplete(payload: BDRApplicationRegulatorReviewSubmitRequestTaskPayload): boolean {
  if (payload.verificationReport) {
    return (
      payload?.regulatorReviewSectionsCompleted?.['BDR'] === true &&
      payload?.regulatorReviewSectionsCompleted?.['OPINION_STATEMENT'] === true &&
      payload?.regulatorReviewSectionsCompleted?.['OVERALL_DECISION'] === true &&
      payload?.regulatorReviewGroupDecisions?.['BDR']?.['type'] === 'ACCEPTED'
    );
  } else {
    return (
      payload?.regulatorReviewSectionsCompleted?.['BDR'] === true &&
      payload?.regulatorReviewGroupDecisions?.['BDR']?.['type'] === 'ACCEPTED'
    );
  }
}

export const outcomeReviewBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
  const outcome = payload?.regulatorReviewOutcome;

  const goingTo27aView: boolean =
    payload?.regulatorReviewOutcome?.hasRegulatorSentFreeAllocation === false &&
    outcome?.hasRegulatorSentHSE === false &&
    outcome?.hasRegulatorSentUSE === false;

  return goingTo27aView ? '../27a-question' : '../use-hse-decision';
};
