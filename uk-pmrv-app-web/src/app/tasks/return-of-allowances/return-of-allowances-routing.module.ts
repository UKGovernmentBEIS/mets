import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { PeerReviewComponent } from '../../shared/components/peer-review/peer-review.component';
import { AnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '../../shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './notify-operator/notify-operator.guard';
import { PeerReviewComponent as ReturnOfAllowancesPeerReviewComponent } from './peer-review/peer-review.component';
import { PeerReviewGuard } from './peer-review/peer-review.guard';
import { SummaryDetailsComponent } from './peer-review/summary-details/summary-details.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { ConfirmationComponent as ReturnedConfirmationComponent } from './returned-allowances/confirmation/confirmation.component';
import { ProvideReturnedDetailsComponent } from './returned-allowances/provide-returned-details/provide-returned-details.component';
import { ProvideReturnedDetailsGuard } from './returned-allowances/provide-returned-details/provide-returned-details.guard';
import { ReturnedAllowancesComponent } from './returned-allowances/returned-allowances.component';
import { SummaryComponent as ReturnedSummaryComponent } from './returned-allowances/summary/summary.component';
import { SummaryGuard as ReturnedSummaryGuard } from './returned-allowances/summary/summary.guard';
import { ProvideDetailsComponent } from './submit/provide-details/provide-details.component';
import { ProvideDetailsGuard } from './submit/provide-details/provide-details.guard';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard } from './submit/summary/summary.guard';
const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Return of allowances' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'provide-details',
        data: { pageTitle: 'Provide details for return of allowances' },
        component: ProvideDetailsComponent,
        canActivate: [ProvideDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Return of allowances details', breadcrumb: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: NotifyOperatorComponent,
        canActivate: [NotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'peer-review',
    data: { pageTitle: 'Peer review' },
    component: PeerReviewComponent,
    canActivate: [PeerReviewGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'peer-review-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Wait peer review' },
        component: PeerReviewWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Provide return of allowances details', breadcrumb: true },
        component: SummaryDetailsComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'roa-peer-review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review' },
        component: ReturnOfAllowancesPeerReviewComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Provide return of allowances details', breadcrumb: true },
        component: SummaryDetailsComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'decision',
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review decision' },
            component: PeerReviewDecisionComponent,
            canActivate: [PeerReviewDecisionGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Peer review decision answers' },
            component: AnswersComponent,
            canActivate: [AnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            data: { pageTitle: 'Peer review decision confirmation' },
            component: ConfirmationComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'returned-allowances',
    children: [
      {
        path: '',
        component: ReturnedAllowancesComponent,
        data: { pageTitle: 'Returned allowances' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'provide-returned-details',
        component: ProvideReturnedDetailsComponent,
        data: { pageTitle: 'Have allowances been returned?' },
        canActivate: [ProvideReturnedDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ReturnedSummaryComponent,
        data: { pageTitle: 'Returned allowances summary' },
        canActivate: [ReturnedSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: ReturnedConfirmationComponent,
        data: { pageTitle: 'Return of allowances complete' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReturnOfAllowancesRoutingModule {}
