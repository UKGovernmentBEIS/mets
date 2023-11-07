import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
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
}

@Component({
  selector: 'app-aggregated-consumption-and-flight-data',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, FlightDataTableComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-flight-data-table
        [headingText]="'File uploaded'"
        [emissionDataDetails]="vm.emissionDataDetails"
      ></app-flight-data-table>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AggregatedConsumptionAndFlightDataComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['aggregatedEmissionsData'],
      emissionDataDetails: payload.aer.aggregatedEmissionsData.aggregatedEmissionDataDetails,
    })),
  );
}
