import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AttachedFile } from '@shared/types/attached-file.type';

import {
  FileInfoDTO,
  RequestActionDTO,
  WasteQDRApplicationSubmittedRequestActionPayload,
  WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class WasteQdrActionService {
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
    WasteQDRApplicationSubmittedRequestActionPayload | WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload
  > {
    return this.store.payload$;
  }

  get payload(): Signal<
    WasteQDRApplicationSubmittedRequestActionPayload | WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload
  > {
    return toSignal(this.payload$);
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().action.payload as WasteQDRApplicationSubmittedRequestActionPayload
    )?.wasteQDRAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getWasteQdrReportFile(qdrReport?: FileInfoDTO): { downloadUrl: string; fileName: string }[] {
    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/document/`;

    return qdrReport
      ? [
          {
            downloadUrl: url + qdrReport.uuid,
            fileName: qdrReport.name,
          },
        ]
      : [];
  }

  getBaseFileDownloadUrl() {
    const actionId = this.store.actionId;
    return `/actions/${actionId}/file-download/attachment/`;
  }
}
