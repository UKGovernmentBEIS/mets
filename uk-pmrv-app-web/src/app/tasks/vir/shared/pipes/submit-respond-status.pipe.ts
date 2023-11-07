import { Pipe, PipeTransform } from '@angular/core';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Pipe({
  name: 'submitRespondStatus',
})
export class SubmitRespondStatusPipe implements PipeTransform {
  transform(virPayload: VirApplicationRespondToRegulatorCommentsRequestTaskPayload, statusKey: string): TaskItemStatus {
    return virPayload?.virRespondToRegulatorCommentsSectionsCompleted?.[statusKey] === true
      ? 'not started'
      : 'cannot start yet';
  }
}
