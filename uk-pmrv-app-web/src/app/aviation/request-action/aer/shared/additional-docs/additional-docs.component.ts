import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { SharedModule } from '@shared/shared.module';

import { EmpAdditionalDocuments, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  hasDocuments: boolean;
  files: { downloadUrl: string; fileName: string }[];
  additionalDocuments: EmpAdditionalDocuments;
  downloadBaseUrl?: string;
}

@Component({
  selector: 'app-additional-docs',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, AerReviewDecisionGroupSummaryComponent],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <h2 app-summary-header class="govuk-heading-m">
        <span [class.govuk-visually-hidden]="!vm.hasDocuments">Uploaded files</span>
      </h2>
      <app-documents-summary-template
        [data]="vm.additionalDocuments"
        [files]="vm.files"
        [isEditable]="false"></app-documents-summary-template>

      <ng-container *ngIf="vm.showDecision">
        <h2 app-summary-header class="govuk-heading-m">Decision Summary</h2>
        <app-aer-review-decision-group-summary
          [data]="vm.reviewDecision"
          [attachments]="vm.reviewAttachments"
          [downloadBaseUrl]="store.aerDelegate.baseFileAttachmentDownloadUrl"></app-aer-review-decision-group-summary>
      </ng-container>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AdditionalDocsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['additionalDocuments'],
      hasDocuments: payload.aer.additionalDocuments.exist,
      files:
        payload.aer.additionalDocuments.documents?.map((uuid) => {
          const file = payload.aerAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      additionalDocuments: payload.aer.additionalDocuments,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'additionalDocuments', true),
    })),
  );
}
