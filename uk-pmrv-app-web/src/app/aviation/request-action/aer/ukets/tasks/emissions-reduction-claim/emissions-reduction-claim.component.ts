import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { EmissionsReductionClaimSummaryTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-summary-template';
import { SharedModule } from '@shared/shared.module';

import { AviationAerSaf, AviationAerSafPurchase, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  saf: AviationAerSaf;
  purchases: {
    purchase: AviationAerSafPurchase;
    files: { downloadUrl: string; fileName: string }[];
  }[];
  declarationFile: { downloadUrl: string; fileName: string };
}

@Component({
  selector: 'app-emissions-reduction-claim',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    EmissionsReductionClaimSummaryTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-aer-emissions-reduction-claim-summary-template
        [data]="vm.saf"
        [purchases]="vm.purchases"
        [declarationFile]="vm.declarationFile"></app-aer-emissions-reduction-claim-summary-template>

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
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['saf'],
      saf: payload.aer.saf,
      purchases: payload.aer.saf.exist
        ? payload.aer.saf.safDetails.purchases.map((item) => ({
            purchase: item,
            files:
              item.evidenceFiles?.map((uuid) => {
                const file = payload.aerAttachments[uuid];
                return {
                  fileName: file,
                  downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
                };
              }) ?? [],
          }))
        : [],
      declarationFile: payload.aer.saf.exist
        ? {
            fileName: payload.aerAttachments[payload.aer.saf.safDetails.noDoubleCountingDeclarationFile],
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${payload.aer.saf.safDetails.noDoubleCountingDeclarationFile}`,
          }
        : null,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'saf', true),
    })),
  );
}
