import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { IndependentReviewCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/independent-review-corsia-template/independent-review-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaIndependentReview } from 'pmrv-api';

interface ViewModel {
  heading: string;
  independentReview: AviationAerCorsiaIndependentReview;
  showDecision: boolean;
}

@Component({
  selector: 'app-independent-review',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    IndependentReviewCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-independent-review-corsia-template [data]="vm.independentReview"></app-independent-review-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="independentReview"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndependentReviewComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectIndependentReview),
  ]).pipe(
    map(([type, independentReview]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.independentReview,
        independentReview: independentReview,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
