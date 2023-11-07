import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { virQuery } from '@aviation/request-action/vir/vir.selectors';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import {
  OperatorImprovementFollowUpResponse,
  OperatorImprovementResponse,
  RegulatorImprovementResponse,
} from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
  regulatorImprovementResponse: RegulatorImprovementResponse;
  operatorImprovementFollowUpResponse: OperatorImprovementFollowUpResponse;
}

@Component({
  selector: 'app-responded',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink, VirSharedModule],
  templateUrl: './responded.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RespondedComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([payload, creationDate]) => ({
      pageHeader: `Follow up response to ${(payload?.verifierUncorrectedItem ?? payload.verifierComment).reference}`,
      creationDate,
      verificationDataItem: payload?.verifierUncorrectedItem ?? payload.verifierComment,
      operatorImprovementResponse: payload.operatorImprovementResponse,
      documentFiles:
        payload.operatorImprovementResponse?.files?.map((uuid) => {
          return {
            fileName: payload.virAttachments[uuid],
            downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      regulatorImprovementResponse: payload.regulatorImprovementResponse,
      operatorImprovementFollowUpResponse: payload.operatorImprovementFollowUpResponse,
    })),
  );

  constructor(public store: RequestActionStore) {}
}
