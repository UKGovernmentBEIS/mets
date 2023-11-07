import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedPermitModule } from '../../permit-application/shared/shared-permit.module';
import { SharedModule } from '../../shared/shared.module';
import { ReviewGroupDecisionComponent as ReviewVariationOperatorLedReviewGroupDecisionComponent } from './review/decision/operator-led/review-group-decision.component';
import { ReviewGroupDecisionComponent as ReviewVariationRegulatorLedReviewGroupDecisionComponent } from './review/decision/regulator-led/review-group-decision.component';
import { ReviewGroupVariationPipe } from './review/decision/review-group-variation.pipe';
import { SummaryDetailsComponent } from './review/determination/summary-details.component';

const declarations = [
  ReviewVariationRegulatorLedReviewGroupDecisionComponent,
  ReviewVariationOperatorLedReviewGroupDecisionComponent,
  SummaryDetailsComponent,
  ReviewGroupVariationPipe,
];

@NgModule({
  declarations: declarations,
  exports: declarations,
  imports: [RouterModule, SharedModule, SharedPermitModule],
})
export class SharedPermitVariationModule {}
