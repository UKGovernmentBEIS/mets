import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export function submitReviewWizardComplete(payload: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload): boolean {
  if (payload.verificationReport) {
    return (
      payload?.regulatorReviewSectionsCompleted?.['BDRS2'] === true &&
      payload?.regulatorReviewSectionsCompleted?.['OPINION_STATEMENT'] === true &&
      payload?.regulatorReviewSectionsCompleted?.['OVERALL_DECISION'] === true &&
      payload?.regulatorReviewGroupDecisions?.['BDRS2']?.['type'] === 'ACCEPTED'
    );
  } else {
    return (
      payload?.regulatorReviewSectionsCompleted?.['BDRS2'] === true &&
      payload?.regulatorReviewGroupDecisions?.['BDRS2']?.['type'] === 'ACCEPTED'
    );
  }
}

export const bdrs2OutcomeReviewBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;

  const goingToCbamView: boolean =
    (payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE' ||
      payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
        'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT') &&
    payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector;
  return goingToCbamView ? '../cbam' : '../installation-sector';
};
