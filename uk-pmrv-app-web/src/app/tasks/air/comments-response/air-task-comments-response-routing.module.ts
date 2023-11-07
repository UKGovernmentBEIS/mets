import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { CommentsResponseContainerComponent } from '@tasks/air/comments-response/comments-response-container.component';
import { OperatorFollowupComponent } from '@tasks/air/comments-response/operator-followup/operator-followup.component';
import { SendReportComponent } from '@tasks/air/comments-response/send-report/send-report.component';
import { SummaryComponent } from '@tasks/air/comments-response/summary/summary.component';
import { SummaryGuard } from '@tasks/air/comments-response/summary/summary.guard';
import { AirImprovementItemResolver } from '@tasks/air/shared/resolvers/air-improvement-item.resolver';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: `Respond to regulator's comments` },
    component: CommentsResponseContainerComponent,
  },
  {
    path: ':id',
    resolve: { airImprovement: AirImprovementItemResolver },
    children: [
      {
        path: 'operator-followup',
        data: { pageTitle: 'Follow-up response to an item' },
        component: OperatorFollowupComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'send-report',
        data: { pageTitle: 'Send report' },
        component: SendReportComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AirTaskCommentsResponseRoutingModule {}
