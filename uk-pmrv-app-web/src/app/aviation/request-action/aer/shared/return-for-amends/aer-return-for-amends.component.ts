import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import {
  AER_CORSIA_REVIEW_APPLICATION_TASKS,
  aerCorsiaReviewGroupMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AER_APPLICATION_TASKS, aerReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { ReturnForAmendsSharedComponent } from '@aviation/shared/components/return-for-amends-shared/return-for-amends-shared.component';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  decisionAmends: { groupKey: string; data: any }[];
  reviewAttachments?: { [key: string]: string };
  downloadBaseUrl?: string;
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
          [creationDate]="vm.creationDate"
          [isAer]="true"
          [isCorsia]="vm.isCorsia"></app-return-for-amends-shared>
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
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(aerCommonQuery.selectIsCorsia),
  ]).pipe(
    map(([payload, creationDate, isCorsia]) => ({
      pageHeader: 'Returned to operator for changes',
      decisionAmends: Object.keys(payload?.reviewGroupDecisions ?? [])
        .filter((key) => payload?.reviewGroupDecisions[key]['type'] === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => {
          const groupKey = isCorsia
            ? Object.keys(aerCorsiaReviewGroupMap).find((reviewKey) => aerCorsiaReviewGroupMap[reviewKey] === key)
            : Object.keys(aerReviewGroupMap).find((reviewKey) => aerReviewGroupMap[reviewKey] === key);
          return {
            groupKey: groupKey,
            data: payload?.reviewGroupDecisions[key],
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
      reviewAttachments: payload?.reviewAttachments,
      creationDate: creationDate,
      downloadBaseUrl: this.store.aerDelegate.baseFileAttachmentDownloadUrl,
      isCorsia: isCorsia,
    })),
  );
}
