import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-corsia-summary-template';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: AviationAerCorsiaMonitoringApproach;
}

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    MonitoringApproachCorsiaSummaryTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-monitoring-approach-corsia-summary-template
        [data]="vm.data"></app-monitoring-approach-corsia-summary-template>

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
export default class MonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectRequestActionPayload),
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, aer, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['monitoringApproach'],
      data: aer.monitoringApproach,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'monitoringApproach', true),
    })),
  );
}
