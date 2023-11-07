import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-action/aer/ukets/aer-ukets.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
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
  imports: [SharedModule, RequestActionTaskComponent, MonitoringApproachSummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-monitoring-approach-summary-template
        [data]="vm.monitoringApproach"
      ></app-monitoring-approach-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
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
    })),
  );
}
