import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnForAmendsSharedComponent } from '@aviation/shared/components/return-for-amends-shared/return-for-amends-shared.component';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload, ItemDTO } from 'pmrv-api';

import { allEmpApplicationTasks, empReviewGroupMap } from '../util/emp.util';

interface ViewModel {
  pageHeader: string;
  decisionAmends: { groupKey: string; data: any }[];
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
  requestTaskType: ItemDTO['taskType'];
}

@Component({
  selector: 'app-return-for-amends',
  template: `
    <div class="govuk-grid-row" *ngIf="vm$ | async as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-return-for-amends-shared
          [pageHeader]="vm.pageHeader"
          [decisionAmends]="vm.decisionAmends"
          [reviewAttachments]="vm.reviewAttachments"
          [downloadBaseUrl]="vm.downloadBaseUrl"
          [requestTaskType]="vm.requestTaskType"
        ></app-return-for-amends-shared>
      </div>
    </div>
  `,
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref, ReturnForAmendsSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmpReturnForAmendsComponent {
  constructor(private store: RequestTaskStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(
      requestTaskQuery.selectRequestTaskPayload,
    ) as Observable<EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload>,
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
  ]).pipe(
    map(([payload, requestTaskType]) => {
      const reviewAttachments = payload.reviewAttachments;
      const empVariationDetailsReviewCompleted = payload.empVariationDetailsReviewCompleted;
      const empVariationDetailsReviewDecision = payload.empVariationDetailsReviewDecision;

      let reviewDecisions: EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload['reviewGroupDecisions']; //TODO consider corsia as well
      let reviewSectionsCompleted: EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload['reviewSectionsCompleted'];
      let empReviewGroup: typeof empReviewGroupMap;

      if (empVariationDetailsReviewCompleted) {
        reviewDecisions = { ...payload.reviewGroupDecisions, EMP_VARIATION_DETAILS: empVariationDetailsReviewDecision };

        reviewSectionsCompleted = {
          ...payload.reviewSectionsCompleted,
          EMP_VARIATION_DETAILS: empVariationDetailsReviewCompleted,
        };

        empReviewGroup = {
          ...empReviewGroupMap,
          empVariationDetails: 'EMP_VARIATION_DETAILS',
        } as unknown as typeof empReviewGroupMap;
      } else {
        reviewDecisions = payload.reviewGroupDecisions;
        reviewSectionsCompleted = payload.reviewSectionsCompleted;
        empReviewGroup = empReviewGroupMap;
      }

      return {
        pageHeader: 'Check your information before sending',
        decisionAmends: Object.keys(reviewDecisions ?? [])
          .filter((key) => reviewDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' && reviewSectionsCompleted[key])
          .map((key) => ({
            groupKey: Object.keys(empReviewGroup).find((reviewKey) => empReviewGroup[reviewKey] === key),
            data: reviewDecisions[key],
          }))
          .sort((a, b) => {
            const allTasks = allEmpApplicationTasks('review')
              .map((t) => t.tasks)
              .reduce((acc, tasks) => acc.concat(tasks), [])
              .map((task) => task.name);

            return allTasks.indexOf(a.groupKey) - allTasks.indexOf(b.groupKey);
          }),
        reviewAttachments,
        downloadBaseUrl: this.store.empDelegate.baseFileAttachmentDownloadUrl,
        requestTaskType,
      };
    }),
  );
}
