import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { EmpOperatorDetails, LimitedCompanyOrganisation, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  operatorDetails: EmpOperatorDetails;
  certificationFiles: { downloadUrl: string; fileName: string }[];
  evidenceFiles: { downloadUrl: string; fileName: string }[];
}

@Component({
  selector: 'app-operator-details',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    OperatorDetailsSummaryTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-operator-details-summary-template
        [data]="vm.operatorDetails"
        [certificationFiles]="vm.certificationFiles"
        [evidenceFiles]="vm.evidenceFiles"></app-operator-details-summary-template>

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
export default class OperatorDetailsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['operatorDetails'],
      operatorDetails: payload.aer.operatorDetails as EmpOperatorDetails,
      certificationFiles:
        payload.aer.operatorDetails?.airOperatingCertificate?.certificateFiles?.map((uuid) => {
          const file = payload.aerAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      evidenceFiles:
        (payload.aer.operatorDetails.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles?.map(
          (uuid) => {
            const file = payload.aerAttachments[uuid];
            return {
              fileName: file,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
            };
          },
        ) ?? [],
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'operatorDetails', true),
    })),
  );
}
