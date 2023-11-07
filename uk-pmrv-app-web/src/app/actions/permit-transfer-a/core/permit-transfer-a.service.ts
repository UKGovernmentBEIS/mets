import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitTransferAApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class PermitTransferAActionService {
  constructor(private readonly store: CommonActionsStore) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments = (
      this.store.getValue().action.payload as PermitTransferAApplicationSubmittedRequestActionPayload
    ).transferAttachments;

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }
}
