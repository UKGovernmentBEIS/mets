import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewComponent as PeerReviewSubmitComponent } from '@tasks/withholding-allowances/peer-review/peer-review.component';
import { PeerReviewWaitComponent } from '@tasks/withholding-allowances/peer-review-wait/peer-review-wait.component';

import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { PeerReviewComponent } from '../../shared/components/peer-review/peer-review.component';
import { SummaryDetailsComponent } from './shared/components/summary-details/summary-details.component';
import { NotifyOperatorGuard } from './submit/guards/notify-operator.guard';
import { PeerReviewGuard } from './submit/guards/peer-review.guard';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { RecoveryDetailsComponent } from './submit/recovery-details/recovery-details.component';
import { SubmitComponent } from './submit/submit.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard } from './submit/summary/summary.guard';
import { NotifyOperatorGuard as WithdrawNotifyOperatorGuard } from './withdraw/guards/notify-operator.guard';
import { WithdrawComponent } from './withdraw/withdraw.component';
import { CloseConfirmationComponent } from './withdraw/withdraw-close/close-confirmation/close-confirmation.component';
import { WithdrawCloseComponent } from './withdraw/withdraw-close/withdraw-close.component';
import { WithdrawNotifyOperatorComponent } from './withdraw/withdraw-notify-operator/withdraw-notify-operator.component';
import { WithdrawReasonComponent } from './withdraw/withdraw-reason/withdraw-reason.component';
import { SummaryGuard as WithdrawSummaryGuard } from './withdraw/withdraw-summary/summary.guard';
import { WithdrawSummaryComponent } from './withdraw/withdraw-summary/withdraw-summary.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Withholding allowances submit' },
        component: SubmitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: NotifyOperatorComponent,
        canActivate: [NotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canActivate: [PeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'recovery-details',
        data: { pageTitle: 'Provide withholding of allowances details' },
        component: RecoveryDetailsComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
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
        data: { pageTitle: 'Provide withholding of allowances details', breadcrumb: true },
        component: SummaryDetailsComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'peer-review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewSubmitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Provide withholding of allowances details', breadcrumb: true },
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
            component: PeerReviewDecisionAnswersComponent,
            canActivate: [PeerReviewDecisionAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            data: { pageTitle: 'Peer review decision confirmation' },
            component: PeerReviewDecisionConfirmationComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'withdraw',
    data: { breadcrumb: 'Withdraw withholding of allowances' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Withdraw withholding of allowances notice' },
        component: WithdrawComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'reason',
        data: { pageTitle: 'Provide reason for withdrawal details' },
        component: WithdrawReasonComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Summary', breadcrumb: true },
        component: WithdrawSummaryComponent,
        canActivate: [WithdrawSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: WithdrawNotifyOperatorComponent,
        canActivate: [WithdrawNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'close',
        children: [
          {
            path: '',
            component: WithdrawCloseComponent,
            data: { pageTitle: 'Close task' },
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            component: CloseConfirmationComponent,
            data: { pageTitle: 'Close task confirmation' },
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WithholdingAllowancesRoutingModule {}
