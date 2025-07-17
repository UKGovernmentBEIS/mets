import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  BDRApplicationSubmittedRequestActionPayload,
  BDRApplicationVerificationSubmittedRequestActionPayload,
  BDRRegulatorReviewReturnedForAmendsRequestActionPayload,
  BDRVerificationReturnedToOperatorRequestActionPayload,
  PeerReviewDecisionSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class BdrActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get requestAction$(): Observable<RequestActionDTO> {
    return this.store.requestAction$;
  }

  get requestAction(): Signal<RequestActionDTO> {
    return toSignal(this.requestAction$);
  }

  get requestActionType$(): Observable<RequestActionDTO['type']> {
    return this.store.requestActionType$;
  }

  get requestActionType(): Signal<RequestActionDTO['type']> {
    return toSignal(this.requestActionType$);
  }

  get payload$(): Observable<
    | BDRApplicationSubmittedRequestActionPayload
    | BDRVerificationReturnedToOperatorRequestActionPayload
    | BDRRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
  > {
    return this.store.payload$;
  }

  get payload(): Signal<
    | BDRApplicationSubmittedRequestActionPayload
    | BDRVerificationReturnedToOperatorRequestActionPayload
    | BDRRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
  > {
    return toSignal(this.payload$);
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as BDRApplicationSubmittedRequestActionPayload
    )?.bdrAttachments;
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
      this.store.getValue().action.payload as BDRApplicationVerificationSubmittedRequestActionPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as BDRRegulatorReviewReturnedForAmendsRequestActionPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getOperatorDownloadUrlBdrFile(bdrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as BDRApplicationSubmittedRequestActionPayload
    )?.bdrAttachments;

    const url = this.getBaseFileDownloadUrl();

    return bdrFile
      ? {
          downloadUrl: url + `${bdrFile}`,
          fileName: attachments[bdrFile],
        }
      : null;
  }

  private getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }
}
