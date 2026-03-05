import { Pipe, PipeTransform } from '@angular/core';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { regulatorImprovementResponseComplete, reviewWizardComplete } from '@tasks/air/review/review.wizard';
import { submitWizardComplete } from '@tasks/air/submit/submit.wizard';

import {
  AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AirApplicationReviewRequestTaskPayload,
  AirApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

@Pipe({
  name: 'taskStatus',
})
export class TaskStatusPipe implements PipeTransform {
  transform(
    airPayload: AirApplicationSubmitRequestTaskPayload | AirApplicationReviewRequestTaskPayload,
    statusKey: string | 'sendReport' | 'provideSummary',
  ): TaskItemStatus {
    switch (airPayload.payloadType) {
      case 'AIR_APPLICATION_SUBMIT_PAYLOAD':
        return this.getSubmitStatus(airPayload as AirApplicationSubmitRequestTaskPayload, statusKey);
      case 'AIR_APPLICATION_REVIEW_PAYLOAD':
        return this.getReviewStatus(airPayload as AirApplicationReviewRequestTaskPayload, statusKey);
      case 'AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD':
        return this.getRespondToRegulatorCommentsStatus(
          airPayload as AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
          statusKey,
        );
      default:
        return 'not started';
    }
  }

  private getSubmitStatus(
    airPayload: AirApplicationSubmitRequestTaskPayload,
    statusKey: string | 'sendReport',
  ): TaskItemStatus {
    if (statusKey === 'sendReport') {
      return submitWizardComplete(airPayload) ? 'not started' : 'cannot start yet';
    }

    if (airPayload?.airSectionsCompleted?.[statusKey] !== undefined) {
      return airPayload?.airSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
    }

    return 'not started';
  }

  /**
   * Rely on actual validation instead of sectionsCompleted lookup,
   * due to future date validate in deadLine field
   *
   * @param airPayload
   * @param statusKey
   * @private
   */
  private getReviewStatus(
    airPayload: AirApplicationReviewRequestTaskPayload,
    statusKey: string | 'sendReport' | 'provideSummary',
  ): TaskItemStatus {
    switch (statusKey) {
      case 'sendReport': {
        return reviewWizardComplete(airPayload) ? 'not started' : 'cannot start yet';
      }
      case 'provideSummary': {
        if (airPayload?.reviewSectionsCompleted?.[statusKey] !== undefined) {
          return airPayload?.reviewSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
        }

        return 'not started';
      }
      default: {
        if (airPayload?.reviewSectionsCompleted?.[statusKey] !== undefined) {
          return airPayload?.reviewSectionsCompleted[statusKey] === true
            ? regulatorImprovementResponseComplete(statusKey, airPayload?.regulatorReviewResponse)
              ? 'complete'
              : 'needs review'
            : 'in progress';
        }

        return 'not started';
      }
    }
  }

  private getRespondToRegulatorCommentsStatus(
    airPayload: AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
    statusKey: string,
  ) {
    if (airPayload?.airRespondToRegulatorCommentsSectionsCompleted?.[statusKey] !== undefined) {
      return airPayload?.airRespondToRegulatorCommentsSectionsCompleted[statusKey] === true
        ? 'complete'
        : 'in progress';
    }

    return 'not started';
  }
}
