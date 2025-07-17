import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template';
import { SharedModule } from '@shared/shared.module';

import { AviationAerDataGaps, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  dataGaps: AviationAerDataGaps;
}

@Component({
  selector: 'app-data-gaps',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    DataGapsSummaryTemplateComponent,
    DataGapsListTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-data-gaps-summary-template [data]="vm.dataGaps"></app-data-gaps-summary-template>
      <ng-container *ngIf="vm.dataGaps.exist">
        <app-data-gaps-list-template [dataGaps]="vm.dataGaps.dataGaps"></app-data-gaps-list-template>
      </ng-container>

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
export default class DataGapsComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['dataGaps'],
      dataGaps: payload.aer.dataGaps,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'dataGaps', true),
    })),
  );
}
