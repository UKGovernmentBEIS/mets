import { Pipe, PipeTransform } from '@angular/core';

import {
  getRespondStatus,
  getReviewStatus,
  getSubmitStatus,
} from '@aviation/shared/components/vir/vir-task-list/vir-task-status.util';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  AviationVirApplicationReviewRequestTaskPayload,
  AviationVirApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

@Pipe({
  name: 'virTaskStatus',
  pure: true,
  standalone: true,
})
export class VirTaskStatusPipe implements PipeTransform {
  transform(
    virPayload: AviationVirApplicationSubmitRequestTaskPayload | AviationVirApplicationReviewRequestTaskPayload,
    statusKey: string | 'sendReport' | 'createSummary',
    isRespondSubmit?: boolean,
  ): TaskItemStatus {
    switch (virPayload.payloadType) {
      case 'AVIATION_VIR_APPLICATION_SUBMIT_PAYLOAD':
        return getSubmitStatus(virPayload, statusKey);
      case 'AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD':
        return getReviewStatus(virPayload, statusKey);
      case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD':
        return getRespondStatus(virPayload, statusKey, isRespondSubmit);
      default:
        return 'not started';
    }
  }
}
