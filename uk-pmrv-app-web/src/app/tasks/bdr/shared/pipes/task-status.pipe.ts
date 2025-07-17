import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { submitReviewWizardComplete } from '@tasks/bdr/review/review.wizard';
import { submitWizardComplete } from '@tasks/bdr/submit/submit.wizard';
import { submitVerificationWizardComplete } from '@tasks/bdr/verification-submit/verification.wizard';

import {
  BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRApplicationSubmitRequestTaskPayload,
  BDRApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskPayload,
} from 'pmrv-api';

import { BdrService } from '../services/bdr.service';

@Pipe({ name: 'taskStatus', standalone: true })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly bdrService: BdrService) {}

  transform(key: string | 'sendReport'): Observable<TaskItemStatus | 'accepted' | 'operator to amend'> {
    return this.bdrService.getPayload().pipe(
      map((payload: RequestTaskPayload) => {
        switch (payload?.payloadType) {
          case 'BDR_APPLICATION_SUBMIT_PAYLOAD':
          case 'BDR_APPLICATION_AMENDS_SUBMIT_PAYLOAD':
            return this.getSubmitStatus(payload as BDRApplicationSubmitRequestTaskPayload, key);
          case 'BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD':
            return this.getVerificationSectionStatus(
              payload as BDRApplicationVerificationSubmitRequestTaskPayload,
              key,
            );
          case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
          case 'BDR_WAIT_FOR_AMENDS_PAYLOAD':
          case 'BDR_APPLICATION_PEER_REVIEW_PAYLOAD':
            return this.getReviewSectionStatus(payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload, key);
          default:
            return 'not started';
        }
      }),
    );
  }

  private getSubmitStatus(
    bdrPayload: BDRApplicationSubmitRequestTaskPayload,
    statusKey: string | 'sendReport',
  ): TaskItemStatus {
    if (statusKey === 'sendReport') {
      return submitWizardComplete(bdrPayload) ? 'not started' : 'cannot start yet';
    }

    if (bdrPayload?.bdrSectionsCompleted?.[statusKey] !== undefined) {
      return bdrPayload?.bdrSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
    }

    return 'not started';
  }

  private getVerificationSectionStatus(
    payload: BDRApplicationVerificationSubmitRequestTaskPayload,
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
    payload: BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
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
      if (statusKey === 'BDR' && payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
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
