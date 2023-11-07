import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { CommentsResponseContainerComponent } from '@tasks/vir/comments-response/comments-response-container.component';
import { OperatorFollowupComponent } from '@tasks/vir/comments-response/operator-followup/operator-followup.component';
import { SendReportComponent } from '@tasks/vir/comments-response/send-report/send-report.component';
import { SummaryComponent } from '@tasks/vir/comments-response/summary/summary.component';
import { SummaryGuard } from '@tasks/vir/comments-response/summary/summary.guard';
import { RegulatorImprovementResponseResolver } from '@tasks/vir/shared/resolvers/regulator-improvement-response.resolver';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: `Respond to regulator's comments` },
    component: CommentsResponseContainerComponent,
  },
  {
    path: ':id',
    resolve: { reference: RegulatorImprovementResponseResolver },
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
export class VirTaskCommentsResponseRoutingModule {}
