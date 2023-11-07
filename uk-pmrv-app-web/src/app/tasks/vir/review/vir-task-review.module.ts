import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { RecommendationResponseReviewComponent } from '@tasks/vir/review/recommendation-response-review/recommendation-response-review.component';
import { SummaryComponent } from '@tasks/vir/review/recommendation-response-review/summary/summary.component';
import { ReviewContainerComponent } from '@tasks/vir/review/review-container.component';
import { VirTaskReviewRoutingModule } from '@tasks/vir/review/vir-task-review-routing.module';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';

import { CreateSummaryComponent } from './create-summary/create-summary.component';
import { SummaryComponent as CreateSummarySummaryComponent } from './create-summary/summary/summary.component';
import { SendReportComponent } from './send-report/send-report.component';

@NgModule({
  declarations: [
    CreateSummaryComponent,
    CreateSummarySummaryComponent,
    RecommendationResponseReviewComponent,
    ReviewContainerComponent,
    SendReportComponent,
    SummaryComponent,
  ],
  imports: [SharedModule, TaskSharedModule, VirSharedModule, VirTaskReviewRoutingModule, VirTaskSharedModule],
})
export class VirTaskReviewModule {}
