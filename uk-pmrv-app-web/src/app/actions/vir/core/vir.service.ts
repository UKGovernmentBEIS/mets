import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import {
  FileInfoDTO,
  VirApplicationRespondedToRegulatorCommentsRequestActionPayload,
  VirApplicationReviewedRequestActionPayload,
  VirApplicationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class VirService {
  constructor(private readonly store: CommonActionsStore) {}

  get payload$(): Observable<
    | VirApplicationSubmittedRequestActionPayload
    | VirApplicationReviewedRequestActionPayload
    | VirApplicationRespondedToRegulatorCommentsRequestActionPayload
  > {
    return this.store.payload$;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const virAttachments = this.attachments || [];

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: virAttachments[id],
      })) ?? []
    );
  }

  getOfficialNoticeFiles(officialNotice?: FileInfoDTO): { downloadUrl: string; fileName: string }[] {
    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/document/`;

    return officialNotice
      ? [
          {
            downloadUrl: url + officialNotice.uuid,
            fileName: officialNotice.name,
          },
        ]
      : [];
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'VIR_APPLICATION_SUBMITTED_PAYLOAD':
        return (<VirApplicationSubmittedRequestActionPayload>payload).virAttachments;
      case 'VIR_APPLICATION_REVIEWED_PAYLOAD':
        return (<VirApplicationReviewedRequestActionPayload>payload).virAttachments;
      case 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD':
        return (<VirApplicationRespondedToRegulatorCommentsRequestActionPayload>payload).virAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }
}
