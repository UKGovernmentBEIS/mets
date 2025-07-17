import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { AnswersComponent } from './answers/answers.component';
import { AnswersGuard } from './answers/answers.guard';
import { ConfirmationComponent } from './confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';
import { PeerReviewDecisionGuard } from './peer-review-decision.guard';
import { PeerReviewSubmittedComponent } from './timeline/peer-review-submitted.component';

export const peerReviewRoutes: Routes = [
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
];

export const peerReviewTimelineRoutes: Routes = [
  {
    path: 'peer-reviewer-submitted',
    data: { pageTitle: 'Peer review submitted' },
    component: PeerReviewSubmittedComponent,
  },
];
