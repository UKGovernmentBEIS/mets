import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { AER_APPLICATION_TASKS, aerReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { ReturnForAmendsSharedComponent } from '@aviation/shared/components/return-for-amends-shared/return-for-amends-shared.component';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { aerQuery } from '../aer-ukets.selectors';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  decisionAmends: { groupKey: string; data: any }[];
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
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
          [creationDate]="vm.creationDate"
          [isAer]="true"
        ></app-return-for-amends-shared>
      </div>
    </div>
  `,
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref, ReturnForAmendsSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerReturnForAmendsComponent {
  constructor(private store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
  ]).pipe(
    map(([payload, creationDate]) => ({
      pageHeader: 'Returned to operator for changes',
      decisionAmends: Object.keys(payload?.reviewGroupDecisions ?? [])
        .filter((key) => payload?.reviewGroupDecisions[key]['type'] === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({
          groupKey: Object.keys(aerReviewGroupMap).find((reviewKey) => aerReviewGroupMap[reviewKey] === key),
          data: payload?.reviewGroupDecisions[key],
        }))
        .sort((a, b) => {
          const allTasks = AER_APPLICATION_TASKS.map((t) => t.tasks)
            .reduce((acc, tasks) => acc.concat(tasks), [])
            .map((task) => task.name);
          return allTasks.indexOf(a.groupKey) - allTasks.indexOf(b.groupKey);
        }),
      reviewAttachments: payload?.reviewAttachments,
      creationDate,
      downloadBaseUrl: this.store.aerDelegate.baseFileAttachmentDownloadUrl,
    })),
  );
}
