import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AerTaskReviewComponent } from '@tasks/aer/shared/components/aer-task-review/aer-task-review.component';
import { ApproachesTierReviewSummaryComponent } from '@tasks/aer/shared/components/approaches-tier-review-summary/approaches-tier-review-summary.component';
import { AerReviewGroupDecisionComponent } from '@tasks/aer/shared/components/decision/aer-review-group-decision/aer-review-group-decision.component';
import { VerificationReviewGroupDecisionComponent } from '@tasks/aer/shared/components/decision/verification-review-group-decision/verification-review-group-decision.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { AerInherentSummaryTemplateComponent } from './components/aer-inherent-summary-template/aer-inherent-summary-template.component';
import { AerTaskComponent } from './components/aer-task/aer-task.component';
import { MeasurementTierReviewSummaryComponent } from './components/measurement-tier-review-summary/measurement-tier-review-summary.component';
import { ReturnLinkComponent } from './components/return-link/return-link.component';
import { DateRangeComponent } from './components/submit/date-range/date-range.component';
import { DeleteComponent } from './components/submit/delete/delete.component';
import { TiersReasonComponent } from './components/submit/tiers-reason/tiers-reason.component';
import { AmendHeadingPipe } from './pipes/amend-heading.pipe';
import { IncludesAnyPipe } from './pipes/includes-any.pipe';
import { TaskPipe } from './pipes/task.pipe';
import { TaskStatusPipe } from './pipes/task-status.pipe';

@NgModule({
  declarations: [
    AerInherentSummaryTemplateComponent,
    AerReviewGroupDecisionComponent,
    AerTaskComponent,
    AerTaskReviewComponent,
    AmendHeadingPipe,
    ApproachesTierReviewSummaryComponent,
    DateRangeComponent,
    DeleteComponent,
    IncludesAnyPipe,
    MeasurementTierReviewSummaryComponent,
    ReturnLinkComponent,
    TaskPipe,
    TaskStatusPipe,
    TiersReasonComponent,
    VerificationReviewGroupDecisionComponent,
  ],
  exports: [
    AerInherentSummaryTemplateComponent,
    AerReviewGroupDecisionComponent,
    AerTaskComponent,
    AerTaskReviewComponent,
    AmendHeadingPipe,
    ApproachesTierReviewSummaryComponent,
    DateRangeComponent,
    DeleteComponent,
    MeasurementTierReviewSummaryComponent,
    ReturnLinkComponent,
    TaskPipe,
    TaskStatusPipe,
    TiersReasonComponent,
    VerificationReviewGroupDecisionComponent,
  ],
  imports: [RouterModule, SharedModule, TaskSharedModule],
})
export class AerSharedModule {}
