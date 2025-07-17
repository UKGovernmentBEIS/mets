import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedNonCompliances } from 'pmrv-api';

interface ViewModel {
  heading: string;
  detailsHeader: string;
  data: AviationAerUncorrectedNonCompliances;
  showDecision: boolean;
}

@Component({
  selector: 'app-non-compliances',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    UncorrectedItemGroupComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  templateUrl: './uncorrected-non-compliances.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedNonCompliancesComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances),
  ]).pipe(
    map(([type, uncorrectedNonCompliances]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.uncorrectedNonCompliances,
        detailsHeader: 'Non-compliances with the Air Navigation Order',
        data: uncorrectedNonCompliances,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
