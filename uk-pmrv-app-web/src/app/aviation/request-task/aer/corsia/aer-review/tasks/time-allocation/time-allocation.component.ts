import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { TimeAllocationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/time-allocation-corsia-template/time-allocation-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaTimeAllocationScope } from 'pmrv-api';

interface ViewModel {
  heading: string;
  timeAllocationScope: AviationAerCorsiaTimeAllocationScope;
  showDecision: boolean;
}

@Component({
  selector: 'app-time-allocation',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    TimeAllocationCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-time-allocation-corsia-template [data]="vm.timeAllocationScope"></app-time-allocation-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="timeAllocationScope"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class TimeAllocationComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectTimeAllocationScope),
  ]).pipe(
    map(([type, timeAllocationScope]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.timeAllocationScope,
        timeAllocationScope: timeAllocationScope,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
