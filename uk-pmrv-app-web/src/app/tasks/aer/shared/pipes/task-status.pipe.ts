import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { StatusKey } from '@tasks/aer/core/aer-task.type';
import {
  getReviewSectionStatus,
  getSectionStatus,
  getVerificationSectionStatus,
} from '@tasks/aer/core/aer-task-statuses';

import {
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
} from 'pmrv-api';

@Pipe({ name: 'taskStatus' })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly aerService: AerService) {}

  transform(key: StatusKey): Observable<TaskItemStatus | 'accepted' | 'operator to amend'> {
    return this.aerService.getPayload().pipe(
      map((payload) => {
        switch (payload?.payloadType) {
          case 'AER_APPLICATION_SUBMIT_PAYLOAD':
          case 'AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
            return getSectionStatus(key, payload as AerApplicationSubmitRequestTaskPayload);
          case 'AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
            return getVerificationSectionStatus(key, payload as AerApplicationVerificationSubmitRequestTaskPayload);
          case 'AER_APPLICATION_REVIEW_PAYLOAD':
          case 'AER_WAIT_FOR_AMENDS_PAYLOAD':
            return getReviewSectionStatus(key, payload as AerApplicationReviewRequestTaskPayload);
          default:
            return 'not started';
        }
      }),
    );
  }
}
