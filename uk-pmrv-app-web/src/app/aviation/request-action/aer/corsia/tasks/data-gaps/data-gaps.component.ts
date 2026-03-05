import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { DataGapsListTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-list-template';
import { calculateAffectedFlightsDataGaps } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps.util';
import { DataGapsSummaryTemplateComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-summary-template-corsia/data-gaps-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaDataGaps, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  dataGaps: AviationAerCorsiaDataGaps;
  affectedFlightsDataGaps: number;
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
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-data-gaps-summary-template
        [data]="vm.dataGaps"
        [affectedFlightsDataGaps]="vm.affectedFlightsDataGaps"></app-data-gaps-summary-template>
      <app-data-gaps-list-template
        *ngIf="vm.dataGaps?.dataGapsDetails?.dataGaps"
        [dataGaps]="vm.dataGaps.dataGapsDetails.dataGaps"></app-data-gaps-list-template>

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
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, aer, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['dataGaps'],
      dataGaps: aer.dataGaps,
      affectedFlightsDataGaps: calculateAffectedFlightsDataGaps(aer.dataGaps?.dataGapsDetails?.dataGaps ?? []),
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'dataGaps', true),
    })),
  );
}
