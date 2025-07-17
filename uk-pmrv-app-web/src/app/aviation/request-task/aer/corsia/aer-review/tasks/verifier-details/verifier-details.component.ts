import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { VerifierDetailsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifier-details-corsia-template/verifier-details-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifierDetails, VerificationBodyDetails } from 'pmrv-api';

interface ViewModel {
  heading: string;
  verificationBodyDetails: VerificationBodyDetails;
  verifierDetails: AviationAerCorsiaVerifierDetails;
  showDecision: boolean;
}

@Component({
  selector: 'app-verifier-details',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    VerifierDetailsCorsiaTemplateComponent,
    AerReviewDecisionGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-verifier-details-corsia-template
        [verificationBodyDetails]="vm.verificationBodyDetails"
        [verifierDetails]="vm.verifierDetails"></app-verifier-details-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="verifierDetails"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectVerificationBodyDetails),
    this.store.pipe(aerVerifyCorsiaQuery.selectVerifierDetails),
  ]).pipe(
    map(([type, verificationBodyDetails, verifierDetails]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.verifierDetails,
        verificationBodyDetails: verificationBodyDetails,
        verifierDetails: verifierDetails,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
