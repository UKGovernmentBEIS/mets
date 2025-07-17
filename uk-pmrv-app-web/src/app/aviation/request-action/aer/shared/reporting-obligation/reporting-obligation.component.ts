import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerReportingObligationDetails, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  reportingData: { reportingRequired: boolean; reportingObligationDetails?: AviationAerReportingObligationDetails };
  files: { downloadUrl: string; fileName: string }[];
  year: number;
}

@Component({
  selector: 'app-reporting-obligation',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    ReportingObligationSummaryTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-reporting-obligation-summary-template
        [reportingData]="vm.reportingData"
        [year]="vm.year"
        [files]="vm.files"></app-reporting-obligation-summary-template>

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
export default class ReportingObligationComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: 'Reporting status',
      reportingData: {
        reportingRequired: payload.reportingRequired,
        reportingObligationDetails: payload.reportingObligationDetails,
      },
      files:
        payload.reportingObligationDetails?.supportingDocuments?.map((uuid) => {
          const file = payload.aerAttachments[uuid];
          return {
            fileName: file,
            downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
          };
        }) ?? [],
      year: payload.reportingYear,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'reportingObligation', true),
    })),
  );
}
