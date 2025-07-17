import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map, Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import { PermanentCessationApplicationSubmittedRequestActionPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class PermanentCessationActionService {
  constructor(private readonly store: CommonActionsStore) {}

  getPermanentCessationPayload$(): Observable<PermanentCessationApplicationSubmittedRequestActionPayload> {
    return this.store.payload$.pipe(map((payload) => payload));
  }

  getPermanentCessationPayload(): Signal<PermanentCessationApplicationSubmittedRequestActionPayload> {
    return toSignal(this.getPermanentCessationPayload$().pipe(map((payload) => payload)));
  }

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const payload = this.store.getValue().action.payload as PermanentCessationApplicationSubmittedRequestActionPayload;
    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: payload.permanentCessationAttachments[id],
      })) ?? []
    );
  }
}
