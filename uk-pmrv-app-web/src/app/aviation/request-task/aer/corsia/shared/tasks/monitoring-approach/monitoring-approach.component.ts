import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-corsia-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaMonitoringApproach;
  showDecision: boolean;
}

@Component({
  selector: 'app-monitoring-approach',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    MonitoringApproachCorsiaSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-monitoring-approach-corsia-summary-template
        [data]="vm.data"></app-monitoring-approach-corsia-summary-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="monitoringApproach"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    map(([type, aer]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.monitoringApproach,
        data: aer.monitoringApproach,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
