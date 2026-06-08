import { inject } from '@angular/core';
import { ResolveFn, Router } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  NERApplicationRegulatorReviewSubmitRequestTaskPayload,
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationVerificationSubmitRequestTaskPayload,
  NERNerDataRegulatorReviewDecision,
  NERVerifiedWithCommentsOverallVerificationAssessment,
  RequestTaskDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { NerPayload } from '.';

export const nerWaitTasks: Array<RequestTaskDTO['type']> = [
  'NER_WAIT_FOR_VERIFICATION',
  'NER_AMEND_WAIT_FOR_VERIFICATION',
  'NER_WAIT_FOR_REVIEW',
  'NER_WAIT_FOR_AMENDS',
  'NER_WAIT_FOR_PEER_REVIEW',
];

export const nerWarningText: Partial<Record<RequestTaskDTO['type'], { text: string; extraText?: string }>> = {
  NER_WAIT_FOR_VERIFICATION: { text: 'Waiting for the verifier to complete the opinion statement' },
  NER_AMEND_WAIT_FOR_VERIFICATION: { text: 'Waiting for the verifier to complete the opinion statement' },
  NER_WAIT_FOR_REVIEW: {
    text: 'Waiting for the regulator to complete the review.',
    extraText: `
    <a 
      href="https://manage-emissions-reporting.service.gov.uk/contact-us"
      rel="noreferrer noopener"
      target="_blank"
      class="govuk-link"
    >Contact your regulator</a> to change or withdraw your NER application.`,
  },
  NER_WAIT_FOR_AMENDS: { text: 'You cannot edit the report as the operator is due to amend it' },
  NER_WAIT_FOR_PEER_REVIEW: { text: 'Waiting for peer review, you cannot make any changes' },
};

export const nerSubmitRequestTasks: Array<RequestTaskDTO['type']> = [
  'NER_APPLICATION_SUBMIT',
  'NER_APPLICATION_AMENDS_SUBMIT',
  'NER_APPLICATION_VERIFICATION_SUBMIT',
  'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT',
];

export const nerVerificationRequestTasks: Array<RequestTaskDTO['type']> = [
  'NER_APPLICATION_VERIFICATION_SUBMIT',
  'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT',
];

export const nerTaskListTitle = (requestTaskType: RequestTaskDTO['type']) => {
  const itemNamePipe = new ItemNamePipe();
  switch (requestTaskType) {
    case 'NER_WAIT_FOR_AMENDS':
      return 'New entrance reserve';
    default:
      return itemNamePipe.transform(requestTaskType);
  }
};

export const nerResolveSectionStatus = (payload: NerPayload, statusKey: string): TaskItemStatus => {
  if (['sendReport'].includes(statusKey)) {
    return nerWizardsCompleted(payload) ? 'not started' : 'cannot start yet';
  }

  switch (payload?.payloadType) {
    case 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
      if (statusKey === 'NER') {
        return 'complete';
      }

      if (payload?.verificationSectionsCompleted?.[statusKey] !== undefined) {
        return payload?.verificationSectionsCompleted[statusKey][0] === true ? 'complete' : 'in progress';
      }

      break;

    case 'NER_APPLICATION_REVIEW_PAYLOAD':
    case 'NER_APPLICATION_PEER_REVIEW_PAYLOAD': {
      const { regulatorReviewSectionsCompleted, regulatorReviewGroupDecisions, regulatorReviewOutcome } = payload ?? {};

      if (regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
        if (statusKey === 'OUTCOME') {
          return regulatorReviewSectionsCompleted?.[statusKey] === true
            ? regulatorReviewOutcome.opinion === 'WITHDRAW'
              ? 'withdrawn'
              : 'complete'
            : 'in progress';
        }

        return (regulatorReviewGroupDecisions?.[statusKey] as NERNerDataRegulatorReviewDecision)?.['type'] ===
          'ACCEPTED'
          ? 'accepted'
          : 'operator to amend';
      }

      return 'undecided';
    }

    default:
      if (payload?.nerSectionsCompleted?.[statusKey] !== undefined) {
        return payload?.nerSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
      }

      break;
  }

  return 'not started';
};

export const nerWizardsCompleted = (payload: NerPayload): boolean => {
  const { nerSectionsCompleted } = payload || {};

  if (payload?.payloadType === 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD') {
    return submitVerificationWizardComplete(payload);
  }

  if (payload?.payloadType === 'NER_APPLICATION_REVIEW_PAYLOAD') {
    return submitReviewWizardComplete(payload);
  }

  if (payload?.payloadType === 'NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
    return nerSectionsCompleted?.['NER'] && nerSectionsCompleted?.['changesRequested'];
  }

  return nerSectionsCompleted?.['NER'];
};

export const wizardIsCompleted = (payload: NerPayload, task: string): boolean => {
  const {
    payloadType,
    ner: { nerFiles, mmpFiles } = {},
    verificationReport: { opinionStatement, overallAssessment } = {},
    regulatorReviewOutcome: { opinion } = {},
  } = payload ?? {};

  switch (payloadType) {
    case 'NER_APPLICATION_SUBMIT_PAYLOAD':
    case 'NER_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
      return !!nerFiles?.file && !!mmpFiles?.file;
    case 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
      switch (task) {
        case 'opinion-statement':
          return !!opinionStatement?.opinionStatementFile;

        case 'overall-decision':
          return (
            (['VERIFIED_WITH_COMMENTS', 'NOT_VERIFIED'].includes(overallAssessment?.type) &&
              !!(overallAssessment as NERVerifiedWithCommentsOverallVerificationAssessment)?.reasons) ||
            overallAssessment?.type === 'VERIFIED_AS_SATISFACTORY'
          );

        default:
          return false;
      }
    case 'NER_APPLICATION_REVIEW_PAYLOAD':
    case 'NER_APPLICATION_PEER_REVIEW_PAYLOAD':
      return !!opinion;

    default:
      return false;
  }
};

export const submitVerificationWizardComplete = (
  payload: NERApplicationVerificationSubmitRequestTaskPayload,
): boolean => {
  const { verificationSectionsCompleted } = payload || {};

  return (
    (verificationSectionsCompleted?.['OPINION_STATEMENT'] || [])[0] &&
    (verificationSectionsCompleted?.['OVERALL_DECISION'] || [])[0]
  );
};

export const submitReviewWizardComplete = (payload: NERApplicationRegulatorReviewSubmitRequestTaskPayload): boolean => {
  const {
    regulatorReviewSectionsCompleted,
    regulatorReviewGroupDecisions: { NER },
  } = payload || {};

  return (
    regulatorReviewSectionsCompleted?.['NER'] &&
    regulatorReviewSectionsCompleted?.['OPINION_STATEMENT'] &&
    regulatorReviewSectionsCompleted?.['OVERALL_DECISION'] &&
    (NER as NERNerDataRegulatorReviewDecision).type === 'ACCEPTED'
  );
};

export const allowReturnForAmends = (requestTaskItem: RequestTaskItemDTO) => {
  const {
    allowedRequestTaskActions,
    requestTask: { payload },
  } = requestTaskItem || {};

  return (
    (
      (payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions
        ?.NER as NERNerDataRegulatorReviewDecision
    )?.type === 'OPERATOR_AMENDS_NEEDED' && allowedRequestTaskActions.includes('NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS')
  );
};

export const changingResolver: ResolveFn<string> = () => {
  const router = inject(Router);

  return router.currentNavigation()?.extras?.state?.changing ?? false;
};
export const nerSendReportBacklinkResolver: ResolveFn<string> = (route) => {
  const sendTo = route.queryParams?.sendTo;

  return sendTo ? './question' : null;
};

export const nerSendReportTitleResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as NerApplicationSubmitRequestTaskPayload;
  return payload?.verificationPerformed ? 'Send to regulator' : 'Send report for verification';
};

export const allowSendForPeerReview = (requestTaskItem: RequestTaskItemDTO): boolean => {
  const { allowedRequestTaskActions, requestTask } = requestTaskItem || {};
  const payload = requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload;

  return (
    nerWizardsCompleted(payload) &&
    allowedRequestTaskActions.includes('NER_REQUEST_PEER_REVIEW') &&
    payload?.regulatorReviewSectionsCompleted?.['OUTCOME'] === true
  );
};

export const allowPeerReviewDecision = (requestTaskItem: RequestTaskItemDTO): boolean => {
  return requestTaskItem.allowedRequestTaskActions.includes('NER_SUBMIT_PEER_REVIEW_DECISION');
};

export const allowCompleteOrWithdraw = (requestTaskItem: RequestTaskItemDTO): boolean => {
  const { allowedRequestTaskActions, requestTask } = requestTaskItem || {};
  const payload = requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload;

  return (
    nerWizardsCompleted(payload) &&
    allowedRequestTaskActions.includes('NER_COMPLETE_REVIEW') &&
    payload?.regulatorReviewSectionsCompleted?.['OUTCOME'] === true
  );
};
