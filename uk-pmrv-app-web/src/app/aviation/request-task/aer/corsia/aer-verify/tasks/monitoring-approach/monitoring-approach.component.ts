import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-corsia-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaMonitoringApproach;
}

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, MonitoringApproachCorsiaSummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-monitoring-approach-corsia-summary-template
        [data]="vm.data"
      ></app-monitoring-approach-corsia-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachComponent {
  vm$: Observable<ViewModel> = this.store.pipe(aerVerifyCorsiaQuery.selectAer).pipe(
    map((aer) => {
      return {
        heading: aerHeaderTaskMap['monitoringApproach'],
        data: aer.monitoringApproach,
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
