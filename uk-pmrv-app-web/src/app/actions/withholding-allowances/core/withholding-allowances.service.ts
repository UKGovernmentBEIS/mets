import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { AttachedFile } from '@shared/types/attached-file.type';

import { FileInfoDTO, WithholdingOfAllowancesApplicationClosedRequestActionPayload } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class WithholdingAllowancesActionService {
  constructor(private readonly store: CommonActionsStore) {}

  getPayload(): Observable<any> {
    return this.store.payload$;
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

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const nonComplianceAttachments = this.attachments || [];

    const actionId = this.store.actionId;
    const url = `/actions/${actionId}/file-download/attachment/`;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: nonComplianceAttachments[id],
      })) ?? []
    );
  }

  private get attachments() {
    const payload = this.store.getValue().action.payload;

    switch (payload.payloadType) {
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED_PAYLOAD':
        return (<WithholdingOfAllowancesApplicationClosedRequestActionPayload>payload)
          ?.withholdingOfAllowancesWithdrawalAttachments;
      default:
        throw Error('Unhandled task type: ' + payload.payloadType);
    }
  }
}
