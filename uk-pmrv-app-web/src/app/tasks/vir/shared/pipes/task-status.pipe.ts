import { Pipe, PipeTransform } from '@angular/core';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { reviewWizardComplete } from '@tasks/vir/review/review.wizard';
import { submitWizardComplete } from '@tasks/vir/submit/submit.wizard';

import {
  VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  VirApplicationReviewRequestTaskPayload,
  VirApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

@Pipe({
  name: 'taskStatus',
})
export class TaskStatusPipe implements PipeTransform {
  transform(
    virPayload: VirApplicationSubmitRequestTaskPayload | VirApplicationReviewRequestTaskPayload,
    statusKey: string | 'sendReport' | 'createSummary',
  ): TaskItemStatus {
    switch (virPayload.payloadType) {
      case 'VIR_APPLICATION_SUBMIT_PAYLOAD':
        return this.getSubmitStatus(virPayload as VirApplicationSubmitRequestTaskPayload, statusKey);
      case 'VIR_APPLICATION_REVIEW_PAYLOAD':
        return this.getReviewStatus(virPayload as VirApplicationReviewRequestTaskPayload, statusKey);
      case 'VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD':
        return this.getRespondToRegulatorCommentsStatus(
          virPayload as VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
          statusKey,
        );
      default:
        return 'not started';
    }
  }

  private getSubmitStatus(
    virPayload: VirApplicationSubmitRequestTaskPayload,
    statusKey: string | 'sendReport',
  ): TaskItemStatus {
    if (statusKey === 'sendReport') {
      return submitWizardComplete(virPayload) ? 'not started' : 'cannot start yet';
    }

    if (virPayload?.virSectionsCompleted?.[statusKey] !== undefined) {
      return virPayload?.virSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
    }

    return 'not started';
  }

  private getReviewStatus(
    virPayload: VirApplicationReviewRequestTaskPayload,
    statusKey: string | 'sendReport' | 'createSummary',
  ): TaskItemStatus {
    switch (statusKey) {
      case 'sendReport': {
        return reviewWizardComplete(virPayload) ? 'not started' : 'cannot start yet';
      }
      default:
        if (virPayload?.reviewSectionsCompleted?.[statusKey] !== undefined) {
          return virPayload?.reviewSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
        }

        return 'not started';
    }
  }

  private getRespondToRegulatorCommentsStatus(
    virPayload: VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
    statusKey: string,
  ) {
    if (virPayload?.virRespondToRegulatorCommentsSectionsCompleted?.[statusKey] !== undefined) {
      return virPayload?.virRespondToRegulatorCommentsSectionsCompleted[statusKey] === true
        ? 'complete'
        : 'in progress';
    }

    return 'not started';
  }
}
