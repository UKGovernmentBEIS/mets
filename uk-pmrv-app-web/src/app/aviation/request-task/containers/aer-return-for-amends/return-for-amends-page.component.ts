import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import {
  AER_CORSIA_REVIEW_APPLICATION_TASKS,
  aerCorsiaReviewGroupMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AER_APPLICATION_TASKS, aerReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnForAmendsSharedComponent } from '@aviation/shared/components/return-for-amends-shared/return-for-amends-shared.component';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ItemDTO } from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  decisionAmends: { groupKey: string; data: any }[];
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
  requestTaskType: ItemDTO['taskType'];
  isCorsia: boolean;
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
          [isAer]="true"
          [isCorsia]="vm.isCorsia"></app-return-for-amends-shared>
      </div>
    </div>
  `,
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref, ReturnForAmendsSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnForAmendsPageComponent {
  constructor(private store: RequestTaskStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectReviewDecisions),
    this.store.pipe(aerQuery.selectReviewAttachments),
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([reviewDecisions, reviewAttachments, requestTaskType, isCorsia]) => ({
      pageHeader: 'Check your information before sending',
      decisionAmends: Object.keys(reviewDecisions ?? [])
        .filter((key) => reviewDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => {
          const groupKey = isCorsia
            ? Object.keys(aerCorsiaReviewGroupMap).find((reviewKey) => aerCorsiaReviewGroupMap[reviewKey] === key)
            : Object.keys(aerReviewGroupMap).find((reviewKey) => aerReviewGroupMap[reviewKey] === key);
          return {
            groupKey: groupKey,
            data: reviewDecisions[key],
          };
        })
        .sort((a, b) => {
          const allTasks = isCorsia ? AER_CORSIA_REVIEW_APPLICATION_TASKS : AER_APPLICATION_TASKS;
          const allTaskNames = allTasks
            .map((t) => t.tasks)
            .reduce((acc, tasks) => acc.concat(tasks), [])
            .map((task) => task.name);
          return allTaskNames.indexOf(a.groupKey) - allTaskNames.indexOf(b.groupKey);
        }),
      reviewAttachments: reviewAttachments,
      downloadBaseUrl: this.store.aerDelegate.baseFileAttachmentDownloadUrl,
      requestTaskType: requestTaskType,
      isCorsia: isCorsia,
    })),
  );
}
