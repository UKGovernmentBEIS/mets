import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

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
        ></app-return-for-amends-shared>
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
  ]).pipe(
    map(([reviewDecisions, reviewAttachments, requestTaskType]) => ({
      pageHeader: 'Check your information before sending',
      decisionAmends: Object.keys(reviewDecisions ?? [])
        .filter((key) => reviewDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({
          groupKey: Object.keys(aerReviewGroupMap).find((reviewKey) => aerReviewGroupMap[reviewKey] === key),
          data: reviewDecisions[key],
        }))
        .sort((a, b) => {
          const allTasks = AER_APPLICATION_TASKS.map((t) => t.tasks)
            .reduce((acc, tasks) => acc.concat(tasks), [])
            .map((task) => task.name);
          return allTasks.indexOf(a.groupKey) - allTasks.indexOf(b.groupKey);
        }),
      reviewAttachments: reviewAttachments,
      downloadBaseUrl: this.store.aerDelegate.baseFileAttachmentDownloadUrl,
      requestTaskType,
    })),
  );
}
