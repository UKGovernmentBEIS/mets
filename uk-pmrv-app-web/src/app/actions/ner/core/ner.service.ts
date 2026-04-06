import { inject, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import { NERApplicationSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class NerActionService {
  private readonly store = inject(CommonActionsStore);

  private getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }

  get payload$(): Observable<NERApplicationSubmittedRequestActionPayload> {
    return this.store.payload$;
  }

  get payload(): Signal<NERApplicationSubmittedRequestActionPayload> {
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

  getOperatorDownloadUrlFile(alrFile: string): AttachedFile {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as NERApplicationSubmittedRequestActionPayload
    )?.nerAttachments;

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
      this.store.getValue().action.payload as NERApplicationSubmittedRequestActionPayload
    )?.nerAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }
}
