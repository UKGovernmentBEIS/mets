import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PeerReviewComponent as SendForPeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { peerReviewRoutes } from '@shared/components/peer-review-decision/peer-review-decision.routes';

import { permanentCessationDetailsGuard, permanentCessationDetailsSummaryGuard } from './guards';
import {
  DetailsOfCessationComponent,
  PermanentCessationDetailsSummaryComponent,
  PermanentCessationNotifyOperatorComponent,
  SubmitContainerComponent,
} from './submit';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Permanent cessation' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'cancel',
        data: { backlink: '../' },
        loadChildren: () => import('../../cancel-task/cancel-task.module').then((m) => m.CancelTaskModule),
      },
      {
        path: 'details',
        children: [
          {
            path: '',
            data: { pageTitle: 'Permanent cessation details' },
            canActivate: [permanentCessationDetailsGuard],
            component: DetailsOfCessationComponent,
          },
          {
            path: 'summary',
            data: { pageTitle: 'Details - summary', breadcrumb: true },
            canActivate: [permanentCessationDetailsSummaryGuard],
            component: PermanentCessationDetailsSummaryComponent,
          },
        ],
      },
      {
        path: 'send-for-peer-review',
        data: { pageTitle: 'Send for peer review' },
        component: SendForPeerReviewComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: PermanentCessationNotifyOperatorComponent,
      },
      ...peerReviewRoutes,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermanentCessationRoutingModule {}
