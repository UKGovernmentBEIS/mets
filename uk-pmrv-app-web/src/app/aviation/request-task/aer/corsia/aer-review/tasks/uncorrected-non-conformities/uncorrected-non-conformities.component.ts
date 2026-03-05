import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities } from 'pmrv-api';

interface ViewModel {
  heading: string;
  uncorrectedNonConformities: AviationAerCorsiaUncorrectedNonConformities;
  showDecision: boolean;
}

@Component({
  selector: 'app-uncorrected-non-conformities',
  standalone: true,
  templateUrl: './uncorrected-non-conformities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    UncorrectedItemGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
})
export class UncorrectedNonConformitiesComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectUncorrectedNonConformities),
  ]).pipe(
    map(([type, uncorrectedNonConformities]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.uncorrectedNonConformities,
        uncorrectedNonConformities: uncorrectedNonConformities,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
