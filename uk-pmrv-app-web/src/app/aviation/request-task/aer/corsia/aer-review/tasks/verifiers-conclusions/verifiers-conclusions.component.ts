import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { VerifiersConclusionsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifiers-conclusions-corsia-template/verifiers-conclusions-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifiersConclusions } from 'pmrv-api';

interface ViewModel {
  heading: string;
  verifiersConclusions: AviationAerCorsiaVerifiersConclusions;
  showDecision: boolean;
}

@Component({
  selector: 'app-verifiers-conclusions',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    VerifiersConclusionsCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-verifiers-conclusions-corsia-template
        [data]="vm.verifiersConclusions"></app-verifiers-conclusions-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="verifiersConclusions"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class VerifiersConclusionsComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectVerifiersConclusions),
  ]).pipe(
    map(([type, verifiersConclusions]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.verifiersConclusions,
        verifiersConclusions: verifiersConclusions,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
