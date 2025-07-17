import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { EmpRequestActionPayload, requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { ReturnForAmendsSharedComponent } from '@aviation/shared/components/return-for-amends-shared/return-for-amends-shared.component';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  decisionAmends: { groupKey: string; data: any }[];
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
}

@Component({
  selector: 'app-emp-return-for-amends',
  template: `
    <div class="govuk-grid-row" *ngIf="vm$ | async as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-return-for-amends-shared
          [pageHeader]="vm.pageHeader"
          [creationDate]="vm.creationDate"
          [decisionAmends]="vm.decisionAmends"
          [reviewAttachments]="vm.reviewAttachments"
          [downloadBaseUrl]="vm.downloadBaseUrl"></app-return-for-amends-shared>
      </div>
    </div>
  `,
  standalone: true,
  imports: [RouterModule, SharedModule, RequestActionTaskComponent, ReturnForAmendsSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmpReturnForAmendsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([payload, creationDate]) => {
      const empVariationDetailsReviewDecision = payload.empVariationDetailsReviewDecision;
      const isVariationDetailsForAmends = empVariationDetailsReviewDecision?.type === 'OPERATOR_AMENDS_NEEDED';

      let reviewDecisions = { ...payload.reviewGroupDecisions };
      let empReviewGroup = empReviewGroupMap;

      if (isVariationDetailsForAmends) {
        reviewDecisions = {
          EMP_VARIATION_DETAILS: empVariationDetailsReviewDecision,
          ...payload.reviewGroupDecisions,
        } as EmpRequestActionPayload['reviewGroupDecisions'];
        empReviewGroup = {
          empVariationDetails: 'EMP_VARIATION_DETAILS',
          ...empReviewGroupMap,
        } as typeof empReviewGroupMap;
      }

      return {
        pageHeader: 'Returned to operator for changes',
        creationDate,
        decisionAmends: Object.keys(reviewDecisions ?? [])
          .filter((key) => reviewDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' || isVariationDetailsForAmends)
          .map((key) => ({
            groupKey: Object.keys(empReviewGroup).find((reviewKey) => empReviewGroup[reviewKey] === key),
            data: reviewDecisions[key],
          })),
        reviewAttachments: payload?.reviewAttachments,
        downloadBaseUrl: this.store.empDelegate.baseFileAttachmentDownloadUrl,
      } as unknown as ViewModel;
    }),
  );
}
