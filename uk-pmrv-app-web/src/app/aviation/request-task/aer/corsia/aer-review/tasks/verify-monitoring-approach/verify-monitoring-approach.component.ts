import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { MonitoringApproachVerifyCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/monitoring-approach-verify-corsia-template/monitoring-approach-verify-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaOpinionStatement } from 'pmrv-api';

interface ViewModel {
  heading: string;
  opinionStatement: AviationAerCorsiaOpinionStatement;
  totalEmissionsProvided: string;
  totalOffsetEmissionsProvided: string;
  showDecision: boolean;
}

@Component({
  selector: 'app-verify-monitoring-approach',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    MonitoringApproachVerifyCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-monitoring-approach-verify-corsia-template
        [opinionStatement]="vm.opinionStatement"
        [totalEmissionsProvided]="vm.totalEmissionsProvided"
        [totalOffsetEmissionsProvided]="
          vm.totalOffsetEmissionsProvided
        "></app-monitoring-approach-verify-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="opinionStatement"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class VerifyMonitoringApproachComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectOpinionStatement),
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
  ]).pipe(
    map(([type, opinionStatement, payload]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.opinionStatement,
        opinionStatement: opinionStatement,
        totalEmissionsProvided: payload.totalEmissionsProvided,
        totalOffsetEmissionsProvided: payload.totalOffsetEmissionsProvided,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
