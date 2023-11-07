import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AttachedFile } from '@shared/types/attached-file.type';

import {
  DoalApplicationAcceptedRequestActionPayload,
  DoalApplicationAcceptedWithCorrectionsRequestActionPayload,
  DoalApplicationClosedRequestActionPayload,
  DoalApplicationProceededToAuthorityRequestActionPayload,
  DoalApplicationRejectedRequestActionPayload,
  FileInfoDTO,
  PeerReviewDecisionSubmittedRequestActionPayload,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class DoalActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get requestAction$(): Observable<any> {
    return this.store.requestAction$;
  }

  getProceededPayload$(): Observable<DoalApplicationProceededToAuthorityRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getClosedPayload$(): Observable<DoalApplicationClosedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getCompletedPayload$(): Observable<
    | DoalApplicationAcceptedRequestActionPayload
    | DoalApplicationAcceptedWithCorrectionsRequestActionPayload
    | DoalApplicationRejectedRequestActionPayload
  > {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getPeerReviewPayload$(): Observable<PeerReviewDecisionSubmittedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getOfficialNoticeFiles(officialNotice?: FileInfoDTO): AttachedFile[] {
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

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const payload = this.store.getValue().action.payload as DoalApplicationProceededToAuthorityRequestActionPayload;
    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: payload.doalAttachments[id],
      })) ?? []
    );
  }
}
