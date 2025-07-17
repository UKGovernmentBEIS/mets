import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { AirImprovementResponseGuard } from '@tasks/air/shared/guards/air-improvement-response.guard';
import { AirImprovementItemResolver } from '@tasks/air/shared/resolvers/air-improvement-item.resolver';
import { ImprovementExistingComponent } from '@tasks/air/submit/improvement-existing/improvement-existing.component';
import { ImprovementNegativeComponent } from '@tasks/air/submit/improvement-negative/improvement-negative.component';
import { ImprovementPositiveComponent } from '@tasks/air/submit/improvement-positive/improvement-positive.component';
import { ImprovementQuestionComponent } from '@tasks/air/submit/improvement-question/improvement-question.component';
import { SendReportComponent } from '@tasks/air/submit/send-report/send-report.component';
import { SubmitContainerComponent } from '@tasks/air/submit/submit-container.component';
import { SummaryComponent } from '@tasks/air/submit/summary/summary.component';
import { SummaryGuard } from '@tasks/air/submit/summary/summary.guard';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { pageTitle: 'Annual improvement report submit' },
        component: SubmitContainerComponent,
      },
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
    path: ':id',
    resolve: { airImprovement: AirImprovementItemResolver },
    children: [
      {
        path: 'improvement-question',
        data: { pageTitle: 'Provide information about this improvement' },
        component: ImprovementQuestionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'improvement-positive',
        data: { pageTitle: 'Provide information about this improvement' },
        component: ImprovementPositiveComponent,
        canActivate: [AirImprovementResponseGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'improvement-negative',
        data: { pageTitle: 'Provide information about this improvement' },
        component: ImprovementNegativeComponent,
        canActivate: [AirImprovementResponseGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'improvement-existing',
        data: { pageTitle: 'Provide information about this improvement' },
        component: ImprovementExistingComponent,
        canActivate: [AirImprovementResponseGuard],
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
export class AirTaskSubmitRoutingModule {}
