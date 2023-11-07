import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { ImprovementResponseReviewComponent } from '@tasks/air/review/improvement-response-review/improvement-response-review.component';
import { SummaryComponent } from '@tasks/air/review/improvement-response-review/summary/summary.component';
import { SummaryGuard } from '@tasks/air/review/improvement-response-review/summary/summary.guard';
import { ProvideSummaryComponent } from '@tasks/air/review/provide-summary/provide-summary.component';
import { SummaryComponent as ProvideSummarySummaryComponent } from '@tasks/air/review/provide-summary/summary/summary.component';
import { SummaryGuard as ProvideSummarySummaryGuard } from '@tasks/air/review/provide-summary/summary/summary.guard';
import { ReviewContainerComponent } from '@tasks/air/review/review-container.component';
import { SendReportComponent } from '@tasks/air/review/send-report/send-report.component';
import { AirImprovementItemResolver } from '@tasks/air/shared/resolvers/air-improvement-item.resolver';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Review annual improvement report' },
    component: ReviewContainerComponent,
  },
  {
    path: 'send-report',
    data: { pageTitle: 'Send report' },
    component: SendReportComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'provide-summary',
    children: [
      {
        path: '',
        data: { pageTitle: 'Provide summary of improvements for official notice' },
        component: ProvideSummaryComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        component: ProvideSummarySummaryComponent,
        canActivate: [ProvideSummarySummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: ':id',
    resolve: { airImprovement: AirImprovementItemResolver },
    children: [
      {
        path: 'improvement-response-review',
        data: { pageTitle: 'Review information about this improvement' },
        component: ImprovementResponseReviewComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AirTaskReviewRoutingModule {}
