import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  ALRApplicationAcceptedRequestActionPayload,
  ALRApplicationAcceptedWithCorrectionsRequestActionPayload,
  ALRApplicationProceededToAuthorityRequestActionPayload,
  ALRApplicationRejectedRequestActionPayload,
  ALRApplicationSubmittedRequestActionPayload,
  ALRApplicationVerificationSubmittedRequestActionPayload,
  ALRRegulatorReviewReturnedForAmendsRequestActionPayload,
  ALRVerificationReturnedToOperatorRequestActionPayload,
  PeerReviewDecisionSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AlrActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get payload$(): Observable<
    | ALRApplicationSubmittedRequestActionPayload
    | ALRVerificationReturnedToOperatorRequestActionPayload
    | ALRRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
    | ALRApplicationAcceptedRequestActionPayload
    | ALRApplicationAcceptedWithCorrectionsRequestActionPayload
    | ALRApplicationRejectedRequestActionPayload
  > {
    return this.store.payload$;
  }

  get payload(): Signal<
    | ALRApplicationSubmittedRequestActionPayload
    | ALRVerificationReturnedToOperatorRequestActionPayload
    | ALRRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
    | ALRApplicationAcceptedRequestActionPayload
    | ALRApplicationAcceptedWithCorrectionsRequestActionPayload
    | ALRApplicationRejectedRequestActionPayload
  > {
    return toSignal(this.payload$);
  }

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

  getOperatorDownloadUrlAlrFile(alrFile: string): AttachedFile {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationSubmittedRequestActionPayload
    )?.alrAttachments;

    return alrFile
      ? {
          downloadUrl: url + `${alrFile}`,
          fileName: attachments[alrFile],
        }
      : null;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationSubmittedRequestActionPayload
    )?.alrAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getVerifierDownloadUrlFiles(files: string[]): AttachedFile[] {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationVerificationSubmittedRequestActionPayload
    )?.verificationAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationProceededToAuthorityRequestActionPayload
    )?.regulatorReviewAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlAlrFile(alrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as ALRApplicationProceededToAuthorityRequestActionPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();

    return alrFile
      ? {
          downloadUrl: url + `${alrFile}`,
          fileName: attachments[alrFile],
        }
      : null;
  }

  private getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }
}
