import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { CivilPenaltyNoticeSubmittedComponent } from './civil-penalty-notice/civil-penalty-notice-submitted/civil-penalty-notice-submitted.component';
import { PeerReviewDecisionComponent as CivilPenaltyNoticePeerReviewDecisionComponent } from './civil-penalty-notice/peer-review-decision/peer-review-decision.component';
import { ClosedComponent } from './closed/closed.component';
import { ConclusionComponent } from './conclusion/conclusion.component';
import { DailyPenaltyNoticeSubmittedComponent } from './daily-penalty-notice/daily-penalty-notice-submitted/daily-penalty-notice-submitted.component';
import { PeerReviewDecisionComponent as DailyPenaltyNoticePeerReviewDecisionComponent } from './daily-penalty-notice/peer-review-decision/peer-review-decision.component';
import { NoticeOfIntentSubmittedComponent } from './notice-of-intent/notice-of-intent-submitted/notice-of-intent-submitted.component';
import { PeerReviewDecisionComponent as NoticeOfIntentPeerReviewDecisionComponent } from './notice-of-intent/peer-review-decision/peer-review-decision.component';
import { NonComplianceSummaryComponent } from './submitted/non-compliance-summary/non-compliance-summary.component';
import { SubmittedComponent } from './submitted/submitted.component';

const routes: Route[] = [
  {
    path: 'submitted',
    data: { pageTitle: 'Non compliance' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Non compliance' },
        component: SubmittedComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Non compliance', breadcrumb: 'Summary' },
        component: NonComplianceSummaryComponent,
      },
    ],
  },
  {
    path: 'closed',
    data: { pageTitle: 'Non compliance closed' },
    component: ClosedComponent,
  },
  {
    path: 'daily-penalty-notice-submitted',
    data: { pageTitle: 'Daily penalty notice submitted' },
    component: DailyPenaltyNoticeSubmittedComponent,
  },
  {
    path: 'dpn-peer-review-decision',
    component: DailyPenaltyNoticePeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'notice-of-intent-submitted',
    data: { pageTitle: 'Notice of intent submitted' },
    component: NoticeOfIntentSubmittedComponent,
  },
  {
    path: 'noi-peer-review-decision',
    component: NoticeOfIntentPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'civil-penalty-notice-submitted',
    data: { pageTitle: 'Civil penalty notice submitted' },
    component: CivilPenaltyNoticeSubmittedComponent,
  },
  {
    path: 'cpn-peer-review-decision',
    component: CivilPenaltyNoticePeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'noi-peer-review-decision',
    component: NoticeOfIntentPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'notice-of-intent-submitted',
    data: { pageTitle: 'Notice of intent submitted' },
    component: NoticeOfIntentSubmittedComponent,
  },
  {
    path: 'conclusion-submitted',
    data: { pageTitle: 'Conclusion submitted' },
    component: ConclusionComponent,
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NonComplianceRoutingModule {}
