import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerCorsiaQuery } from '@aviation/request-action/aer/corsia/aer-corsia.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
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
  imports: [SharedModule, RequestActionTaskComponent, MonitoringApproachCorsiaSummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-monitoring-approach-corsia-summary-template
        [data]="vm.data"
      ></app-monitoring-approach-corsia-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MonitoringApproachComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerCorsiaQuery.selectAer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([aer, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: aerHeaderTaskMap['monitoringApproach'],
      data: aer.monitoringApproach,
    })),
  );
}
