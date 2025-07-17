import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { empHeaderTaskMap, empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { VariationDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/variation-details-summary-template/variation-details-summary-template.component';
import { VariationDetailsSummaryTemplateComponent as CorsiaVariationDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/variation-details-summary-template/variation-details-summary-template.component';

import { EmpVariationReviewDecision, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: any; // EmpVariationUkEtsDetails | EmpVariationCorsiaDetails;
  reasonRegulatorLed?: any; //EmpVariationUkEtsRegulatorLedReason | string;
  showDecision: boolean;
  showVariationDecision: boolean;
  reviewDecision?: EmpVariationReviewDecision;
  reviewAttachments?: { [key: string]: string };
  variationDecision?: EmpVariationReviewDecision;
  downloadBaseUrl?: string;
}

@Component({
  selector: 'app-variation-details',
  standalone: true,
  imports: [
    CommonModule,
    RequestActionTaskComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
    VariationDetailsSummaryTemplateComponent,
    CorsiaVariationDetailsSummaryTemplateComponent,
  ],
  templateUrl: './variation-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  constructor(public store: RequestActionStore) {}

  isCorsia$ = this.store.pipe(requestActionQuery.selectIsCorsia);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: empHeaderTaskMap['empVariationDetails'],
      data: payload.empVariationDetails,
      ...((requestActionType === 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED' ||
        requestActionType === 'EMP_VARIATION_UKETS_APPLICATION_APPROVED') &&
      regulatorViewer
        ? {
            showDecision: true,
            showVariationDecision: false,
            reviewDecision: payload.empVariationDetailsReviewDecision,
            reviewAttachments: payload.reviewAttachments,
            downloadBaseUrl: this.store.empDelegate.baseFileAttachmentDownloadUrl,
          }
        : (requestActionType === 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED' ||
              requestActionType === 'EMP_VARIATION_UKETS_APPLICATION_APPROVED') &&
            regulatorViewer
          ? {
              showDecision: false,
              showVariationDecision: true,
              variationDecision: payload.reviewGroupDecisions[empReviewGroupMap['empVariationDetails']],
              reviewAttachments: payload.reviewAttachments,
              downloadBaseUrl: this.store.empDelegate.baseFileAttachmentDownloadUrl,
            }
          : {
              showDecision: false,
              showVariationDecision: false,
            }),
      reasonRegulatorLed: payload.reasonRegulatorLed,
    })),
  );
}
