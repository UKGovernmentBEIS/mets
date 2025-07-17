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

import { OperatorImprovementResponse, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
}

@Component({
  selector: 'app-reference-item',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, VirSharedModule],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.verificationDataItem.reference | verificationReferenceTitle"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-verification-recommendation-item
        [verificationDataItem]="vm.verificationDataItem"></app-verification-recommendation-item>
      <app-operator-response-item
        [reference]="vm.verificationDataItem.reference"
        [operatorImprovementResponse]="vm.operatorImprovementResponse"
        [attachedFiles]="vm.documentFiles"
        [isAviation]="true"
        [isEditable]="false"></app-operator-response-item>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReferenceItemComponent {
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
    })),
  );
}
