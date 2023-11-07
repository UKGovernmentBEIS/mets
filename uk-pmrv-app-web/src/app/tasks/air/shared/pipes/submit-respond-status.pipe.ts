import { Pipe, PipeTransform } from '@angular/core';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Pipe({
  name: 'submitRespondStatus',
})
export class SubmitRespondStatusPipe implements PipeTransform {
  transform(airPayload: AirApplicationRespondToRegulatorCommentsRequestTaskPayload, statusKey: string): TaskItemStatus {
    return airPayload?.airRespondToRegulatorCommentsSectionsCompleted?.[statusKey] === true
      ? 'not started'
      : 'cannot start yet';
  }
}
