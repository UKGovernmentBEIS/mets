import { Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { DreApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class DreService {
  constructor(private readonly store: CommonActionsStore) {}

  getPayload(): Observable<any> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const dreAttachments = this.attachments || [];

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: dreAttachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;
    switch (payload.payloadType) {
      case 'DRE_APPLICATION_SUBMITTED_PAYLOAD':
        return (<DreApplicationSubmittedRequestActionPayload>payload).dreAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }
}
