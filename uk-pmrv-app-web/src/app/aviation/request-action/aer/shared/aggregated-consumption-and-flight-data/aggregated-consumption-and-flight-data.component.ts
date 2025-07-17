import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { AerDecisionViewModel, getAerDecisionReview } from '@aviation/request-action/aer/shared/util/aer.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/aer/aer-review-decision-group-summary/aer-review-decision-group-summary.component';
import { FlightDataTableComponent } from '@aviation/shared/components/aer/flight-data-table';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsiaAggregatedEmissionDataDetails,
  AviationAerUkEtsAggregatedEmissionDataDetails,
  RequestActionDTO,
} from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  emissionDataDetails:
    | AviationAerUkEtsAggregatedEmissionDataDetails[]
    | AviationAerCorsiaAggregatedEmissionDataDetails[];
  isCorsia: boolean;
}

@Component({
  selector: 'app-aggregated-consumption-and-flight-data',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, FlightDataTableComponent, AerReviewDecisionGroupSummaryComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true">
      <app-flight-data-table
        [headingText]="'File uploaded'"
        [emissionDataDetails]="vm.emissionDataDetails"
        [isCorsia]="vm.isCorsia"></app-flight-data-table>

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
export default class AggregatedConsumptionAndFlightDataComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel & AerDecisionViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectIsCorsia),
  ]).pipe(
    map(([payload, requestActionType, regulatorViewer, isCorsia]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['aggregatedEmissionsData'],
      emissionDataDetails: payload.aer.aggregatedEmissionsData.aggregatedEmissionDataDetails,
      ...getAerDecisionReview(payload, requestActionType, regulatorViewer, 'aggregatedEmissionsData', true),
      isCorsia: isCorsia,
    })),
  );
}
