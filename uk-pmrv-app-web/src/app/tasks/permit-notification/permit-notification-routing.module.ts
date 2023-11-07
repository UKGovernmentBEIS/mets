import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { AuthGuard } from '@core/guards/auth.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { ChangeAssigneeComponent } from '../../change-task-assignee/components/change-assignee/change-assignee.component';
import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { PeerReviewComponent } from '../../shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '../../shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewComponent as NotificationPeerReview } from './peer-review/peer-review.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { DecisionComponent } from './review/decision/decision.component';
import { InvalidDataComponent } from './review/invalid-data/invalid-data.component';
import { NotifyOperatorComponent } from './review/notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './review/notify-operator/notify-operator.guard';
import { PeerReviewGuard } from './review/peer-review/peer-review.guard';
import { ReviewComponent } from './review/review.component';
import { ReviewWaitComponent } from './review-wait/review-wait.component';
import { AnswersComponent } from './submit/answers/answers.component';
import { AnswersGuard } from './submit/answers/answers.guard';
import { DescriptionComponent } from './submit/description/description.component';
import { DescriptionGuard } from './submit/description/description.guard';
import { DetailsOfChangeComponent } from './submit/details-of-change/details-of-change.component';
import { DetailsOfChangeGuard } from './submit/details-of-change/details-of-change.guard';
import { SubmitContainerComponent as SubmitNotificationComponent } from './submit/submit/submit-container.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard } from './submit/summary/summary.guard';

const routes: Route[] = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Notify the regulator of a change' },
        component: SubmitContainerComponent,
      },
      {
        path: 'details-of-change',
        data: { pageTitle: 'Notify the regulator of a change - choose type of notification' },
        component: DetailsOfChangeComponent,
        canActivate: [DetailsOfChangeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'description',
        data: {
          pageTitle: 'Notify the regulator of a change - describe type of notification',
          backlink: '../details-of-change',
        },
        component: DescriptionComponent,
        canActivate: [DescriptionGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Notify the regulator of a change - Confirm answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Notify the regulator of a change - Summary', breadcrumb: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: 'submit',
        data: {
          pageTitle: 'Submit',
          caption: 'Notify the regulator of a change',
          titleSubmitted: 'Notification submitted',
        },
        component: SubmitNotificationComponent,
      },
      {
        path: 'tasks',
        loadChildren: () => import('./../../tasks/tasks.module').then((m) => m.TasksModule),
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        component: ReviewComponent,
        data: { pageTitle: 'Review notification of a change' },
      },
      {
        path: 'tasks/:taskId/change-assignee',
        component: ChangeAssigneeComponent,
        data: { pageTitle: 'Change assignee' },
      },
      {
        path: 'rfi',
        canActivate: [AuthGuard],
        loadChildren: () => import('../../rfi/rfi.module').then((m) => m.RfiModule),
      },
      {
        path: 'decision',
        component: DecisionComponent,
        data: { pageTitle: 'Review notification of a change' },
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
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review' },
            component: PeerReviewComponent,
            canActivate: [PeerReviewGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'invalid-data',
        data: {
          pageTitle: 'Invalid data',
        },
        component: InvalidDataComponent,
      },
    ],
  },
  {
    path: 'review-wait',
    component: ReviewWaitComponent,
    data: { pageTitle: 'Permit notification wait for review' },
  },
  {
    path: 'peer-review-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Wait peer review' },
        component: PeerReviewWaitComponent,
      },
    ],
  },
  {
    path: 'peer-review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review' },
        component: NotificationPeerReview,
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
    path: 'follow-up',
    loadChildren: () => import('./follow-up/follow-up.module').then((m) => m.FollowUpModule),
  },
  {
    path: 'file-download/:uuid',
    component: FileDownloadComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitNotificationRoutingModule {}
