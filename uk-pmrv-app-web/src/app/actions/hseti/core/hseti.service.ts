import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map, Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  FileInfoDTO,
  HSETI,
  HSETIApplicationSubmittedRequestActionPayload,
  HSETIRegulatorReviewReturnedForAmendsRequestActionPayload,
  PeerReviewDecisionSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class HseTiActionService {
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
    | HSETIApplicationSubmittedRequestActionPayload
    | HSETIRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
  > {
    return this.store.payload$;
  }

  get payload(): Signal<
    | HSETIApplicationSubmittedRequestActionPayload
    | HSETIRegulatorReviewReturnedForAmendsRequestActionPayload
    | PeerReviewDecisionSubmittedRequestActionPayload
  > {
    return toSignal(this.payload$);
  }

  get allocationPeriod$(): Observable<string> {
    return this.payload$.pipe(
      map((payload) => {
        const hseti = (payload as HSETIApplicationSubmittedRequestActionPayload)?.hseti as HSETI;
        const period = hseti?.allocationPeriod?.split('_');
        return `${period[1]}-${period[2]}`;
      }),
    );
  }

  get allocationPeriod(): Signal<string> {
    return toSignal(this.allocationPeriod$);
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as HSETIApplicationSubmittedRequestActionPayload
    )?.hsetiAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getOperatorDownloadUrlHseTiFile(bdrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as HSETIApplicationSubmittedRequestActionPayload
    )?.hsetiAttachments;

    const url = this.getBaseFileDownloadUrl();

    return bdrFile
      ? {
          downloadUrl: url + `${bdrFile}`,
          fileName: attachments[bdrFile],
        }
      : null;
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as HSETIRegulatorReviewReturnedForAmendsRequestActionPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();
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

  private getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }
}
