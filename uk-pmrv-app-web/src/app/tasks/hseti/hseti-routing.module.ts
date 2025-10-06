import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { ReviewTaskListComponent } from './review';
import { DetailsReviewComponent } from './review/details-review/details-review.component';
import { HsetiOverallDecisionSummaryGuard } from './review/guards/overall-decision-summary.guard';
import { HsetiNotifyOperatorComponent } from './review/notify-operator/notify-operator.component';
import { HSETIOverallDecisionReviewReasonComponent } from './review/overall-decision/decision-reason/decision-reason.component';
import { DecisionSummaryComponent } from './review/overall-decision/decision-summary/decision-summary.component';
import { HSETIOverallDecisionReviewComponent } from './review/overall-decision/overall-decision.component';
import { HsetiReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { PageTitleResolver } from './shared/resolvers/page-title.resolver';
import { HSETIDetailsComponent, SendReportComponent, SubmitContainerComponent, SummaryComponent } from './submit';
import { HsetiChangesRequestedComponent } from './submit/changes-requested/hseti-changes-requested.component';
import { DetailsSummaryGuard } from './submit/guards';
import { HseTiSendReportConfirmationComponent } from './submit/send-report/confirmation/confirmation.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: {
          pageTitleTemplate: 'Complete {{allocationPeriod}} HSE target increase application',
        },
        resolve: { pageTitle: PageTitleResolver },
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
            data: {
              pageTitleTemplate: 'Upload the {{allocationPeriod}} HSE target increase file',
              backlink: '../',
            },
            resolve: { pageTitle: PageTitleResolver },
            component: HSETIDetailsComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
              breadcrumb: 'Summary',
            },
            component: SummaryComponent,
            providers: [DetailsSummaryGuard],
            canActivate: [DetailsSummaryGuard],
          },
        ],
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: HsetiChangesRequestedComponent,
      },
      {
        path: 'send-report',
        data: {
          pageTitleTemplate: 'Submit {{allocationPeriod}} HSE target increase application',
          breadcrumb: 'Send report',
        },
        resolve: { pageTitle: PageTitleResolver },
        component: SendReportComponent,
      },
      {
        path: 'confirmation',
        component: HseTiSendReportConfirmationComponent,
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: {
          pageTitleTemplate: 'Review {{allocationPeriod}} HSE target increase application',
        },
        resolve: { pageTitle: PageTitleResolver },
        component: ReviewTaskListComponent,
      },
      {
        path: 'details',
        data: { pageTitle: 'Review HSE target increase details', groupKey: 'HSETI', breadcrumb: true },

        component: DetailsReviewComponent,
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amends', breadcrumb: true },
            component: HsetiReturnForAmendsComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'overall-decision',
        children: [
          {
            path: '',
            data: {
              pageTitleTemplate: '{{allocationPeriod}} HSE target increase application overall decision',
              groupKey: 'OVERALL_DECISION',
              breadcrumb: true,
            },
            resolve: { pageTitle: PageTitleResolver },
            component: HSETIOverallDecisionReviewComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'reason',
            data: {
              pageTitleTemplate: '{{allocationPeriod}} HSE target increase application overall decision - {{decision}}',
              groupKey: 'OVERALL_DECISION',
              backlink: '../',
            },
            resolve: { pageTitle: PageTitleResolver },
            component: HSETIOverallDecisionReviewReasonComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitleTemplate: '{{allocationPeriod}} HSE target increase application summary',
              breadcrumb: 'Check your answers',
            },
            resolve: { pageTitle: PageTitleResolver },
            component: DecisionSummaryComponent,
            canActivate: [HsetiOverallDecisionSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator of decision' },
        component: HsetiNotifyOperatorComponent,
        canActivate: [PaymentCompletedGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Send for peer review', breadcrumb: true },
            component: PeerReviewComponent,
            canActivate: [PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'peer-review-decision',
        providers: [PeerReviewDecisionGuard],
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review decision' },
            component: PeerReviewDecisionComponent,
            canActivate: [PeerReviewDecisionGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Peer review decision answers', breadcrumb: 'Summary' },
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
    path: 'peer-review-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Wait peer review' },
        component: ReviewTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
  {
    path: 'payment-not-completed',
    component: PaymentNotCompletedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HseTiRoutingModule {}
