import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaEmissionsReductionClaim, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaEmissionsReductionClaim;
  cefFiles: AttachedFile[];
  declarationFiles: AttachedFile[];
}

@Component({
  selector: 'app-emissions-reduction-claim',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    AerEmissionsReductionClaimCorsiaTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-aer-emissions-reduction-claim-corsia-template
        [emissionsReductionClaim]="vm.data"
        [cefFiles]="vm.cefFiles"
        [declarationFiles]="vm.declarationFiles"></app-aer-emissions-reduction-claim-corsia-template>

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
export default class EmissionsReductionClaimComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(aerCorsiaQuery.selectAerAttachments),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, aer, attachments, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['emissionsReductionClaim'],
      data: aer.emissionsReductionClaim,
      cefFiles:
        aer.emissionsReductionClaim.emissionsReductionClaimDetails?.cefFiles?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      declarationFiles:
        aer.emissionsReductionClaim.emissionsReductionClaimDetails?.noDoubleCountingDeclarationFiles?.map((uuid) => {
          const file = attachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'emissionsReductionClaim', true),
    })),
  );
}
