import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import {
  NonComplianceApplicationClosedRequestActionPayload,
  NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload,
  NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload,
  NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class NonComplianceService {
  constructor(
    private readonly store: CommonActionsStore,
    private readonly router: Router,
  ) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';
    const nonComplianceAttachments = this.attachments || [];

    const actionId = this.store.actionId;
    const url = `${isAviation}/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: nonComplianceAttachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;

    switch (payload.payloadType) {
      case 'NON_COMPLIANCE_APPLICATION_CLOSED_PAYLOAD':
        return (<NonComplianceApplicationClosedRequestActionPayload>payload).nonComplianceAttachments;
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD':
        return (<NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload>payload)
          .nonComplianceAttachments;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD':
        return (<NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload>payload).nonComplianceAttachments;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD':
        return (<NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload>payload).nonComplianceAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }
}
