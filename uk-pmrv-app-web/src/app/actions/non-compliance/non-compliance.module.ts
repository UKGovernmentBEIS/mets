import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { ActionSharedModule } from '../shared/action-shared-module';
import { CivilPenaltyNoticeSubmittedComponent } from './civil-penalty-notice/civil-penalty-notice-submitted/civil-penalty-notice-submitted.component';
import { PeerReviewDecisionComponent as CivilPenaltyPeerReviewDecisionComponent } from './civil-penalty-notice/peer-review-decision/peer-review-decision.component';
import { ClosedComponent } from './closed/closed.component';
import { ConclusionComponent } from './conclusion/conclusion.component';
import { DailyPenaltyNoticeSubmittedComponent } from './daily-penalty-notice/daily-penalty-notice-submitted/daily-penalty-notice-submitted.component';
import { PeerReviewDecisionComponent as DailyPenaltyNoticePeerReviewDecisionComponent } from './daily-penalty-notice/peer-review-decision/peer-review-decision.component';
import { NonComplianceRoutingModule } from './non-compliance-routing.module';
import { NoticeOfIntentSubmittedComponent } from './notice-of-intent/notice-of-intent-submitted/notice-of-intent-submitted.component';
import { PeerReviewDecisionComponent as NoticeOfIntentPeerReviewDecisionComponent } from './notice-of-intent/peer-review-decision/peer-review-decision.component';
import { NonComplianceSummaryComponent } from './submitted/non-compliance-summary/non-compliance-summary.component';
import { SubmittedComponent } from './submitted/submitted.component';

@NgModule({
  declarations: [
    CivilPenaltyNoticeSubmittedComponent,
    CivilPenaltyPeerReviewDecisionComponent,
    ClosedComponent,
    ConclusionComponent,
    DailyPenaltyNoticePeerReviewDecisionComponent,
    DailyPenaltyNoticeSubmittedComponent,
    NonComplianceSummaryComponent,
    NoticeOfIntentPeerReviewDecisionComponent,
    NoticeOfIntentSubmittedComponent,
    SubmittedComponent,
  ],
  imports: [ActionSharedModule, NonComplianceRoutingModule, SharedModule],
})
export class NonComplianceModule {}
