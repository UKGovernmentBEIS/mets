import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerRecommendedImprovements;
  showDecision: boolean;
}

@Component({
  selector: 'app-recommended-improvements',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    RecommendedImprovementsGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './recommended-improvements.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(recommendedImprovementsQuery.selectRecommendedImprovements),
    this.store.pipe(aerVerifyCorsiaQuery.selectIndependentReview),
  ]).pipe(
    map(([type, recommendedImprovements]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.recommendedImprovements,
        data: recommendedImprovements,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
