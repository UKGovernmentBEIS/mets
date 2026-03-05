import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { virQuery } from '@aviation/request-action/vir/vir.selectors';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { OperatorImprovementResponse, RegulatorImprovementResponse, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
  regulatorImprovementResponse: RegulatorImprovementResponse;
}

@Component({
  selector: 'app-recommendation-response-item',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, VirSharedModule],
  templateUrl: './recommendation-response-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendationResponseItemComponent {
  verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  constructor(
    public store: RequestActionStore,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      verificationDataItem: this.verificationDataItem,
      operatorImprovementResponse: payload.operatorImprovementResponses[this.verificationDataItem.reference],
      documentFiles:
        payload.operatorImprovementResponses[this.verificationDataItem.reference]?.files?.map((uuid) => {
          const file = payload.virAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      regulatorImprovementResponse:
        payload.regulatorReviewResponse.regulatorImprovementResponses[this.verificationDataItem.reference],
    })),
  );
}
