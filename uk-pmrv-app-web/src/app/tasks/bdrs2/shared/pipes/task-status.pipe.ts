import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { submitWizardComplete } from '@tasks/bdrs2/utils';

import { BDRS2ApplicationSubmitRequestTaskPayload, RequestTaskPayload } from 'pmrv-api';

@Pipe({ name: 'taskStatus', standalone: true })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly bdrs2Service: BdrS2Service) {}

  transform(key: string | 'sendReport'): Observable<TaskItemStatus | 'accepted' | 'operator to amend'> {
    return this.bdrs2Service.getPayload().pipe(
      map((payload: RequestTaskPayload) => {
        switch (payload?.payloadType) {
          case 'BDRS2_APPLICATION_SUBMIT_PAYLOAD':
            return this.getSubmitStatus(payload as BDRS2ApplicationSubmitRequestTaskPayload, key);

          default:
            return 'not started';
        }
      }),
    );
  }

  private getSubmitStatus(
    bdrs2Payload: BDRS2ApplicationSubmitRequestTaskPayload,
    statusKey: string | 'sendReport',
  ): TaskItemStatus {
    if (statusKey === 'sendReport') {
      return submitWizardComplete(bdrs2Payload) ? 'not started' : 'cannot start yet';
    }

    if (bdrs2Payload?.bdrs2SectionsCompleted?.[statusKey] !== undefined) {
      return bdrs2Payload?.bdrs2SectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
    }

    return 'not started';
  }
}
