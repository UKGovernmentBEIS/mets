import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { MonitoringApproachSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-summary-template';
import { EmissionSmallEmittersSupportFacilityFormValues } from '@aviation/shared/components/aer/monitoring-approach-summary-template/monitoring-approach.interfaces';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerSmallEmittersMonitoringApproach,
  AviationAerSupportFacilityMonitoringApproach,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  monitoringApproach: EmissionSmallEmittersSupportFacilityFormValues;
}

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    MonitoringApproachSummaryTemplateComponent,
    AerReviewDecisionGroupSummaryComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-monitoring-approach-summary-template
        [data]="vm.monitoringApproach"></app-monitoring-approach-summary-template>

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
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['monitoringApproach'],
      monitoringApproach: {
        monitoringApproachType: payload.aer.monitoringApproach.monitoringApproachType,
        totalEmissionsType:
          (payload.aer.monitoringApproach as AviationAerSupportFacilityMonitoringApproach)?.totalEmissionsType ?? null,
        fullScopeTotalEmissions:
          (payload.aer.monitoringApproach as AviationAerSupportFacilityMonitoringApproach)?.fullScopeTotalEmissions ??
          null,
        aviationActivityTotalEmissions:
          (payload.aer.monitoringApproach as AviationAerSupportFacilityMonitoringApproach)
            ?.aviationActivityTotalEmissions ?? null,
        numOfFlightsJanApr:
          (payload.aer.monitoringApproach as AviationAerSmallEmittersMonitoringApproach)?.numOfFlightsJanApr ?? null,
        numOfFlightsMayAug:
          (payload.aer.monitoringApproach as AviationAerSmallEmittersMonitoringApproach)?.numOfFlightsMayAug ?? null,
        numOfFlightsSepDec:
          (payload.aer.monitoringApproach as AviationAerSmallEmittersMonitoringApproach)?.numOfFlightsSepDec ?? null,
        totalEmissions:
          (payload.aer.monitoringApproach as AviationAerSmallEmittersMonitoringApproach)?.totalEmissions ?? null,
      },
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'monitoringApproach', true),
    })),
  );
}
