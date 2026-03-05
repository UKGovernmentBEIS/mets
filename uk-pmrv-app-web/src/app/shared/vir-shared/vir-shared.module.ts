import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { RecommendationResponseItemFormComponent } from '@shared/vir-shared/components/recommendation-response-item-form/recommendation-response-item-form.component';
import { RespondItemFormComponent } from '@shared/vir-shared/components/respond-item-form/respond-item-form.component';

import { OperatorFollowupItemComponent } from './components/operator-followup-item/operator-followup-item.component';
import { OperatorResponseDataItemComponent } from './components/operator-response-data-item/operator-response-data-item.component';
import { OperatorResponseItemComponent } from './components/operator-response-item/operator-response-item.component';
import { ReferenceItemFormComponent } from './components/reference-item-form/reference-item-form.component';
import { RegulatorCreateSummaryComponent } from './components/regulator-create-summary/regulator-create-summary.component';
import { RegulatorResponseItemComponent } from './components/regulator-response-item/regulator-response-item.component';
import { VerificationDataItemComponent } from './components/verification-data-item/verification-data-item.component';
import { VerificationRecommendationItemComponent } from './components/verification-recommendation-item/verification-recommendation-item.component';
import { VerificationReferenceTitlePipe } from './pipes/verification-reference-title.pipe';

@NgModule({
  declarations: [
    OperatorFollowupItemComponent,
    OperatorResponseDataItemComponent,
    OperatorResponseItemComponent,
    RecommendationResponseItemFormComponent,
    ReferenceItemFormComponent,
    RegulatorCreateSummaryComponent,
    RegulatorResponseItemComponent,
    RespondItemFormComponent,
    VerificationDataItemComponent,
    VerificationRecommendationItemComponent,
    VerificationReferenceTitlePipe,
  ],
  exports: [
    OperatorFollowupItemComponent,
    OperatorResponseDataItemComponent,
    OperatorResponseItemComponent,
    RecommendationResponseItemFormComponent,
    ReferenceItemFormComponent,
    RegulatorCreateSummaryComponent,
    RegulatorResponseItemComponent,
    RespondItemFormComponent,
    VerificationDataItemComponent,
    VerificationRecommendationItemComponent,
    VerificationReferenceTitlePipe,
  ],
  imports: [RouterModule, SharedModule],
})
export class VirSharedModule {}
