import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { EmpAdditionalDocuments } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: EmpAdditionalDocuments;
  showDecision: boolean;
  files: AttachedFile[];
}

@Component({
  selector: 'app-additional-docs',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, AerReviewDecisionGroupComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-documents-summary-template [data]="vm.data" [files]="vm.files"></app-documents-summary-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="additionalDocuments"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([type, aer, aerAttachments]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.additionalDocuments,
        data: aer.additionalDocuments,
        showDecision: showReviewDecisionComponent.includes(type),
        files:
          aer.additionalDocuments.documents?.map((id) => ({
            downloadUrl: `${
              (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
            }/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
