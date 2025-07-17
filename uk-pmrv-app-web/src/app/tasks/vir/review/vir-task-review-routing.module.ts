import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { CreateSummaryComponent } from '@tasks/vir/review/create-summary/create-summary.component';
import { RecommendationResponseReviewComponent } from '@tasks/vir/review/recommendation-response-review/recommendation-response-review.component';
import { SummaryComponent } from '@tasks/vir/review/recommendation-response-review/summary/summary.component';
import { SummaryGuard } from '@tasks/vir/review/recommendation-response-review/summary/summary.guard';
import { ReviewContainerComponent } from '@tasks/vir/review/review-container.component';
import { SendReportComponent } from '@tasks/vir/review/send-report/send-report.component';
import { ResponseItemResolver } from '@tasks/vir/shared/resolvers/response-item.resolver';

import { SummaryComponent as CreateSummarySummaryComponent } from './create-summary/summary/summary.component';
import { SummaryGuard as CreateSummarySummaryGuard } from './create-summary/summary/summary.guard';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: '', data: { pageTitle: 'Review verifier improvement report' }, component: ReviewContainerComponent },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
  {
    path: 'send-report',
    data: { pageTitle: 'Send report' },
    component: SendReportComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'create-summary',
    children: [
      {
        path: '',
        data: { pageTitle: 'Create report summary' },
        component: CreateSummaryComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        component: CreateSummarySummaryComponent,
        canActivate: [CreateSummarySummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: ':id',
    resolve: { verificationDataItem: ResponseItemResolver },
    children: [
      {
        path: 'recommendation-response-review',
        data: { pageTitle: 'Respond to an item' },
        component: RecommendationResponseReviewComponent,
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
export class VirTaskReviewRoutingModule {}
