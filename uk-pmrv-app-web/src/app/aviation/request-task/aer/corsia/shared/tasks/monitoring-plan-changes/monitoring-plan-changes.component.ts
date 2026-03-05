import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerMonitoringPlanChangesSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-plan-changes-summary-template/monitoring-plan-changes-summary-template.component';
import { AerMonitoringPlanVersionsComponent } from '@aviation/shared/components/aer/monitoring-plan-versions';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerMonitoringPlanChanges, AviationAerMonitoringPlanVersion } from 'pmrv-api';

interface ViewModel {
  heading: string;
  planVersions: AviationAerMonitoringPlanVersion[];
  monitoringPlanChanges: AviationAerMonitoringPlanChanges;
  showDecision: boolean;
}

@Component({
  selector: 'app-monitoring-plan-changes',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    AerMonitoringPlanChangesSummaryTemplateComponent,
    AerMonitoringPlanVersionsComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-aer-monitoring-plan-versions [planVersions]="vm.planVersions"></app-aer-monitoring-plan-versions>
      <app-aer-monitoring-plan-changes-summary-template
        [data]="vm.monitoringPlanChanges"></app-aer-monitoring-plan-changes-summary-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="aerMonitoringPlanChanges"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanChangesComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
  ]).pipe(
    map(([type, payload, aer]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.aerMonitoringPlanChanges,
        planVersions: payload.aerMonitoringPlanVersions,
        monitoringPlanChanges: aer.aerMonitoringPlanChanges,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
