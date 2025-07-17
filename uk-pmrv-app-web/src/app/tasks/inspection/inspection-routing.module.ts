import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PeerReviewComponent as PeerReviewSubmitComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';

import { FollowUpActionRespondComponent } from './respond/follow-up-action/follow-up-action.component';
import { FollowUpResponseSummaryComponent } from './respond/follow-up-response-summary/follow-up-response-summary.component';
import { FollowUpRespondSummaryGuard } from './respond/follow-up-response-summary/follow-up-summary.guard';
import { FollowUpSendReportComponent } from './respond/send-report/send-report.component';
import { DetailsSummaryComponent } from './shared/details-summary/details-summary.component';
import { detailsSummaryGuard } from './shared/details-summary/details-summary.guard';
import { InspectionItemResolver } from './shared/resolvers/follow-up-action.resolver';
import { pageTitleResolver } from './shared/resolvers/page-title.resolver';
import { TaskListResponseContainerComponent } from './shared/task-list-response/task-list-response-container.component';
import { TaskListSubmitContainerComponent } from './shared/task-list-submit/task-list-submit-container.component';
import { DetailsComponent } from './submit/details/details.component';
import { FollowUpActionSubmitComponent } from './submit/follow-up-action/follow-up-action.component';
import { FollowUpActionDeleteComponent } from './submit/follow-up-action-delete/follow-up-action-delete.component';
import { FollowUpActionsGuardQuestionComponent } from './submit/follow-up-action-guard-question/follow-up-action-guard-question.component';
import { FollowUpActionsComponent } from './submit/follow-up-actions/follow-up-actions.component';
import { FollowUpSummaryComponent } from './submit/follow-up-summary/follow-up-summary.component';
import { FollowUpSubmitSummaryGuard } from './submit/follow-up-summary/follow-up-summary.guard';
import { InspectionNotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { ResponseDeadlineComponent } from './submit/response-deadline/response-deadline.component';
import { SendPeerReviewGuard } from './submit/send-peer-review.guard';

const commonRoutes = [
  {
    path: 'details-summary',
    data: {
      pageTitle: 'Check your answers',
      breadcrumb: 'Report details actions summary',
    },
    component: DetailsSummaryComponent,
    canActivate: [detailsSummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'follow-up-summary',
    data: {
      pageTitle: 'Check your answers',
      breadcrumb: 'Follow-up actions summary',
      isCheckYourAnswersPage: true,
    },
    component: FollowUpSummaryComponent,
    canActivate: [FollowUpSubmitSummaryGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

export const routes: Routes = [
  {
    path: ':type',
    children: [
      {
        path: 'submit',
        children: [
          {
            path: '',
            resolve: {
              pageTitle: pageTitleResolver,
            },
            component: TaskListSubmitContainerComponent,
          },
          {
            path: 'change-assignee',
            loadChildren: () =>
              import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
          ...commonRoutes,
          {
            path: 'details',
            data: { pageTitle: 'Inspection details' },
            component: DetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'follow-up-actions-guard-question',
            data: { pageTitle: 'Do you want to add follow-up actions for the operator?' },
            component: FollowUpActionsGuardQuestionComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'follow-up-actions',
            data: { pageTitle: 'Follow-up actions needed', backlink: '../follow-up-actions-guard-question' },
            component: FollowUpActionsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'response-deadline',
            data: { pageTitle: 'Set a response deadline', backlink: '../follow-up-actions' },
            component: ResponseDeadlineComponent,
          },
          {
            path: 'peer-review',
            data: { pageTitle: 'Peer review' },
            component: PeerReviewSubmitComponent,
            canActivate: [SendPeerReviewGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'notify-operator',
            data: { pageTitle: 'Notify operator' },
            component: InspectionNotifyOperatorComponent,
          },
          {
            path: ':id',
            resolve: { followUpAction: InspectionItemResolver },
            children: [
              {
                path: 'add-follow-up-action',
                data: { pageTitle: 'Add a follow-up action' },
                component: FollowUpActionSubmitComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'delete-follow-up-action',
                data: { pageTitle: 'Delete a follow up action', breadcrumb: 'Delete a follow up action' },
                component: FollowUpActionDeleteComponent,
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
        ],
      },
      {
        path: 'peer-review-wait',
        resolve: {
          pageTitle: pageTitleResolver,
        },
        component: TaskListSubmitContainerComponent,
      },
      {
        path: 'peer-review',
        resolve: {
          pageTitle: pageTitleResolver,
        },
        children: [
          { path: '', component: TaskListSubmitContainerComponent },
          {
            path: 'change-assignee',
            loadChildren: () =>
              import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
          ...commonRoutes,
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
        path: 'respond',
        children: [
          {
            path: '',
            resolve: {
              pageTitle: pageTitleResolver,
            },
            component: TaskListResponseContainerComponent,
          },
          {
            path: 'change-assignee',
            loadChildren: () =>
              import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
          {
            path: 'details-summary',
            data: {
              pageTitle: 'Check your answers',
              breadcrumb: 'Report details actions summary',
            },
            component: DetailsSummaryComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'send-report',
            data: { pageTitle: 'Send to regulator', breadcrumb: true },
            component: FollowUpSendReportComponent,
          },
          {
            path: ':actionId',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Check your answers',
                  breadcrumb: 'Follow-up responses summary',
                },
                component: FollowUpResponseSummaryComponent,
                canActivate: [FollowUpRespondSummaryGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'follow-up-action',
                data: { pageTitle: 'Follow-up action' },
                component: FollowUpActionRespondComponent,
                canDeactivate: [PendingRequestGuard],
              },
            ],
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
export class InspectionRoutingModule {}
