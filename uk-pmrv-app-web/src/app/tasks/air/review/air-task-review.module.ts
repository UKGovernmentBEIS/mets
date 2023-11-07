import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { SharedModule } from '@shared/shared.module';
import { AirTaskReviewRoutingModule } from '@tasks/air/review/air-task-review-routing.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ImprovementResponseReviewComponent } from './improvement-response-review/improvement-response-review.component';
import { SummaryComponent } from './improvement-response-review/summary/summary.component';
import { SummaryGuard } from './improvement-response-review/summary/summary.guard';
import { ProvideSummaryComponent } from './provide-summary/provide-summary.component';
import { SummaryComponent as ProvideSummarySummaryComponent } from './provide-summary/summary/summary.component';
import { SummaryGuard as ProvideSummaryGuard } from './provide-summary/summary/summary.guard';
import { ReviewContainerComponent } from './review-container.component';
import { SendReportComponent } from './send-report/send-report.component';

@NgModule({
  declarations: [
    ImprovementResponseReviewComponent,
    ProvideSummaryComponent,
    ProvideSummarySummaryComponent,
    ReviewContainerComponent,
    SendReportComponent,
    SummaryComponent,
  ],
  imports: [AirSharedModule, AirTaskReviewRoutingModule, AirTaskSharedModule, SharedModule, TaskSharedModule],
  providers: [ProvideSummaryGuard, SummaryGuard],
})
export class AirTaskReviewModule {}
