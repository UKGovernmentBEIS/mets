import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCommonQuery } from '@aviation/request-action/aer/shared/aer-common.selector';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMonitoringPlanChanges, AviationAerMonitoringPlanVersion, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  planVersions: AviationAerMonitoringPlanVersion[];
  planChanges: AviationAerMonitoringPlanChanges;
}

@Component({
  selector: 'app-monitoring-plan-changes',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    AerMonitoringPlanVersionsComponent,
    AerMonitoringPlanChangesSummaryTemplateComponent,
  ],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-aer-monitoring-plan-versions [planVersions]="vm.planVersions"></app-aer-monitoring-plan-versions>
      <app-aer-monitoring-plan-changes-summary-template
        [data]="vm.planChanges"
      ></app-aer-monitoring-plan-changes-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MonitoringPlanChangesComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCommonQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['aerMonitoringPlanChanges'],
      planVersions: payload.aerMonitoringPlanVersions,
      planChanges: payload.aer.aerMonitoringPlanChanges,
    })),
  );
}
