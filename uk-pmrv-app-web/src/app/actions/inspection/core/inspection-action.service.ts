import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AttachedFile } from '@shared/types/attached-file.type';

import {
  InstallationInspectionApplicationSubmittedRequestActionPayload,
  InstallationInspectionOperatorRespondedRequestActionPayload,
  PeerReviewDecisionSubmittedRequestActionPayload,
} from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class InspectionActionService {
  constructor(private readonly store: CommonActionsStore) {}

  get requestAction$(): Observable<any> {
    return this.store.requestAction$;
  }

  getPeerReviewPayload$(): Observable<PeerReviewDecisionSubmittedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getOnSiteAuditPayload$(): Observable<InstallationInspectionApplicationSubmittedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getOnSiteAuditResondedPayload$(): Observable<InstallationInspectionOperatorRespondedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const payload = this.store.getValue().action
      .payload as InstallationInspectionApplicationSubmittedRequestActionPayload;
    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: payload.inspectionAttachments[id],
      })) ?? []
    );
  }
}
