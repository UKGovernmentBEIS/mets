import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  ALRApplicationSubmittedRequestActionPayload,
  ALRApplicationVerificationSubmittedRequestActionPayload,
  ALRVerificationReturnedToOperatorRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AlrActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get payload$(): Observable<
    ALRApplicationSubmittedRequestActionPayload | ALRVerificationReturnedToOperatorRequestActionPayload
  > {
    return this.store.payload$;
  }

  get payload(): Signal<
    ALRApplicationSubmittedRequestActionPayload | ALRVerificationReturnedToOperatorRequestActionPayload
  > {
    return toSignal(this.payload$);
  }

  get requestActionType$(): Observable<RequestActionDTO['type']> {
    return this.store.requestActionType$;
  }

  get requestActionType(): Signal<RequestActionDTO['type']> {
    return toSignal(this.requestActionType$);
  }

  getOperatorDownloadUrlAlrFile(alrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationSubmittedRequestActionPayload
    )?.alrAttachments;
    const url = this.getBaseFileDownloadUrl();

    return alrFile
      ? {
          downloadUrl: url + `${alrFile}`,
          fileName: attachments[alrFile],
        }
      : null;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationSubmittedRequestActionPayload
    )?.alrAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getVerifierDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationVerificationSubmittedRequestActionPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  private getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }
}
