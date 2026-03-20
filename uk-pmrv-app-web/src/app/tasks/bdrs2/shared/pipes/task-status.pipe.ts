import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { submitReviewWizardComplete } from '@tasks/bdrs2/review';
import { submitWizardComplete } from '@tasks/bdrs2/utils';
import { submitVerificationWizardComplete } from '@tasks/bdrs2/verification-submit/verification.wizard';

import {
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2ApplicationSubmitRequestTaskPayload,
  BDRS2ApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskPayload,
} from 'pmrv-api';

@Pipe({ name: 'taskStatus', standalone: true })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly bdrs2Service: BdrS2Service) {}

  transform(key: string | 'sendReport'): Observable<TaskItemStatus | 'accepted' | 'operator to amend'> {
    return this.bdrs2Service.getPayload().pipe(
      map((payload: RequestTaskPayload) => {
        switch (payload?.payloadType) {
          case 'BDRS2_APPLICATION_SUBMIT_PAYLOAD':
          case 'BDRS2_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
            return this.getSubmitStatus(payload as BDRS2ApplicationSubmitRequestTaskPayload, key);
          case 'BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
            return this.getVerificationSectionStatus(
              payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload,
              key,
            );
          case 'BDRS2_WAIT_FOR_AMENDS_PAYLOAD':
          case 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
          case 'BDRS2_APPLICATION_PEER_REVIEW_PAYLOAD':
            return this.getReviewSectionStatus(payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload, key);

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

  private getVerificationSectionStatus(
    payload: BDRS2ApplicationVerificationSubmitRequestTaskPayload,
    statusKey: string | 'sendReport',
  ): TaskItemStatus {
    if (statusKey === 'sendReport') {
      return submitVerificationWizardComplete(payload) ? 'not started' : 'cannot start yet';
    }

    if (statusKey === 'baseline') {
      return 'complete';
    }

    if (payload?.verificationSectionsCompleted?.[statusKey]?.[0] !== undefined) {
      return payload?.verificationSectionsCompleted?.[statusKey]?.[0] === true ? 'complete' : 'in progress';
    }
    return 'not started';
  }

  private getReviewSectionStatus(
    payload: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
    statusKey: string | 'outcome',
  ): TaskItemStatus {
    if (statusKey === 'outcome') {
      return submitReviewWizardComplete(payload)
        ? payload?.regulatorReviewSectionsCompleted?.[statusKey] === true
          ? 'complete'
          : payload?.regulatorReviewSectionsCompleted?.[statusKey] === false
            ? 'in progress'
            : 'not started'
        : 'cannot start yet';
    } else {
      if (statusKey === 'BDRS2' && payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
        return payload.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'ACCEPTED'
          ? 'accepted'
          : 'operator to amend';
      } else if (
        (statusKey === 'OPINION_STATEMENT' || statusKey === 'OVERALL_DECISION') &&
        payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined
      ) {
        return payload.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'ACCEPTED' ? 'accepted' : 'undecided';
      } else {
        return 'undecided';
      }
    }
  }
}
