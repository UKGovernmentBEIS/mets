import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedMisstatements } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerUncorrectedMisstatements;
  showDecision: boolean;
}

@Component({
  selector: 'app-uncorrected-misstatements',
  standalone: true,
  imports: [
    SharedModule,
    UncorrectedItemGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
    ReturnToLinkComponent,
  ],
  templateUrl: './uncorrected-misstatements.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedMisstatementsComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(uncorrectedMisstatementsQuery.selectUncorrectedMisstatements),
  ]).pipe(
    map(([type, misstatements]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.uncorrectedMisstatements,
        data: misstatements,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
