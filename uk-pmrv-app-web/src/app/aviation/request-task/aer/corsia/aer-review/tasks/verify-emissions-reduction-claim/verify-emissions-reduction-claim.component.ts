import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { EmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/emissions-reduction-claim-corsia-template/emissions-reduction-claim-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaEmissionsReductionClaimVerification } from 'pmrv-api';

interface ViewModel {
  heading: string;
  emissionsReductionClaimVerification: AviationAerCorsiaEmissionsReductionClaimVerification;
  showDecision: boolean;
}

@Component({
  selector: 'app-verify-emissions-reduction-claim',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    EmissionsReductionClaimCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-emissions-reduction-claim-corsia-template
        [data]="vm.emissionsReductionClaimVerification"></app-emissions-reduction-claim-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="emissionsReductionClaimVerification"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class VerifyEmissionsReductionClaimComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectEmissionsReductionClaimVerification),
  ]).pipe(
    map(([type, emissionsReductionClaimVerification]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.emissionsReductionClaimVerification,
        emissionsReductionClaimVerification: emissionsReductionClaimVerification,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
