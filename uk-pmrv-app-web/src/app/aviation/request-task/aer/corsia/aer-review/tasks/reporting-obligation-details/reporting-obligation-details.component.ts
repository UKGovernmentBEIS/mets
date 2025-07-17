import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerReviewCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-review/aer-review-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ReportingObligationSummaryTemplateComponent } from '@aviation/shared/components/aer/reporting-obligation-summary-template/reporting-obligation-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaRequestMetadata, AviationAerReportingObligationDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  year: number;
  data: AviationAerReportingObligationDetails;
  reportingRequired: boolean;
  files: AttachedFile[];
  showDecision: boolean;
}

@Component({
  selector: 'app-reporting-obligation-details',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    AerReviewDecisionGroupComponent,
    ReportingObligationSummaryTemplateComponent,
  ],
  templateUrl: './reporting-obligation-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportingObligationDetailsComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(aerReviewCorsiaQuery.selectReportingObligationDetails),
    this.store.pipe(aerReviewCorsiaQuery.selectReportingObligationRequired),
    this.store.pipe(aerReviewCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([type, requestInfo, reportingObligationDetails, reportingRequired, aerAttachments]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.reportingObligation,
        data: reportingObligationDetails,
        reportingRequired,
        showDecision: showReviewDecisionComponent.includes(type),
        year: (requestInfo.requestMetadata as AviationAerCorsiaRequestMetadata).year,
        files:
          reportingObligationDetails.supportingDocuments?.map((id) => ({
            downloadUrl: `${(this.store.aerDelegate as AerCorsiaStoreDelegate).baseFileAttachmentDownloadUrl}/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
      };
    }),
  );
}
