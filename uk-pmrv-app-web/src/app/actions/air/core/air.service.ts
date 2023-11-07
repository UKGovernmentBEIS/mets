import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { AttachedFile } from '@shared/types/attached-file.type';

import {
  AirApplicationRespondedToRegulatorCommentsRequestActionPayload,
  AirApplicationReviewedRequestActionPayload,
  AirApplicationSubmittedRequestActionPayload,
  FileInfoDTO,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class AirService {
  constructor(private readonly store: CommonActionsStore) {}

  get payload$(): Observable<
    | AirApplicationSubmittedRequestActionPayload
    | AirApplicationReviewedRequestActionPayload
    | AirApplicationRespondedToRegulatorCommentsRequestActionPayload
  > {
    return this.store.payload$;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments = this.operatorAttachments || [];

    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments = this.regulatorAttachments || [];

    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
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

  private createBaseFileDownloadUrl(): string {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }

  private get operatorAttachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'AIR_APPLICATION_SUBMITTED_PAYLOAD':
        return (<AirApplicationSubmittedRequestActionPayload>payload).airAttachments;
      case 'AIR_APPLICATION_REVIEWED_PAYLOAD':
        return (<AirApplicationReviewedRequestActionPayload>payload).airAttachments;
      case 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD':
        return (<AirApplicationRespondedToRegulatorCommentsRequestActionPayload>payload).airAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }

  private get regulatorAttachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'AIR_APPLICATION_REVIEWED_PAYLOAD':
        return (<AirApplicationReviewedRequestActionPayload>payload).reviewAttachments;
      case 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD':
        return (<AirApplicationRespondedToRegulatorCommentsRequestActionPayload>payload).reviewAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }
}
