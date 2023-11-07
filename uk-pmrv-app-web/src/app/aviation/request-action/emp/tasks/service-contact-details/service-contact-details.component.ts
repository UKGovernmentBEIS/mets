import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { ServiceContactDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp/service-contact-details-summary-template/service-contact-details-summary-template.component';

import { ServiceContactDetails } from 'pmrv-api';

interface ViewModel {
  data: ServiceContactDetails;
}

@Component({
  selector: 'app-service-contact-details',
  standalone: true,
  imports: [
    CommonModule,
    RequestActionTaskComponent,
    EmpReviewDecisionGroupSummaryComponent,
    ServiceContactDetailsSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
  ],
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-service-contact-details-summary-template [data]="vm.data"></app-service-contact-details-summary-template>
      <app-emp-review-decision-group-summary
        *ngIf="vm.showDecision"
        [data]="vm.reviewDecision"
        [attachments]="vm.reviewAttachments"
        [downloadBaseUrl]="vm.downloadBaseUrl"
      ></app-emp-review-decision-group-summary>
      <app-emp-variation-review-decision-group-summary
        *ngIf="vm.showVariationDecision"
        [data]="vm.variationDecision"
        [attachments]="vm.reviewAttachments"
        [downloadBaseUrl]="vm.downloadBaseUrl"
      ></app-emp-variation-review-decision-group-summary>
      <ng-container *ngIf="vm.showVariationRegLedDecision">
        <h2 class="govuk-heading-m">List changes to include in the variation schedule</h2>
        <app-emp-variation-regulator-led-decision-group-summary
          [data]="vm.variationRegLedDecision"
        ></app-emp-variation-regulator-led-decision-group-summary>
      </ng-container>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceContactDetailsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      data: payload.serviceContactDetails,
      ...getEmpSubmittedViewModelData(
        requestActionType,
        payload,
        regulatorViewer,
        this.store.empDelegate.baseFileAttachmentDownloadUrl,
        'serviceContactDetails',
      ),
    })),
  );
}
