import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { OverallDecisionGroupComponent } from '@aviation/shared/components/aer-verify/overall-decision-group/overall-decision-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerNotVerifiedDecision,
  AviationAerVerifiedSatisfactoryDecision,
  AviationAerVerifiedSatisfactoryWithCommentsDecision,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  data:
    | AviationAerVerifiedSatisfactoryDecision
    | AviationAerVerifiedSatisfactoryWithCommentsDecision
    | AviationAerNotVerifiedDecision;
  isCorsia: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-overall-decision',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    OverallDecisionGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './overall-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(overallDecisionQuery.selectOverallDecision),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([type, overallDecision, isCorsia]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.overallDecision,
        data: overallDecision,
        showDecision: showReviewDecisionComponent.includes(type),
        isCorsia: isCorsia,
      };
    }),
  );
}
