import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { ActionSharedModule } from '../shared/action-shared-module';
import { DreRoutingModule } from './dre-routing.module';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { OfficialNoticeRecipientsComponent } from './shared/official-notice-recipients/official-notice-recipients.component';
import { DreSummaryComponent } from './submitted/dre-summary/dre-summary.component';
import { SubmittedComponent } from './submitted/submitted.component';

@NgModule({
  declarations: [
    DreSummaryComponent,
    OfficialNoticeRecipientsComponent,
    PeerReviewDecisionComponent,
    SubmittedComponent,
  ],
  imports: [ActionSharedModule, DreRoutingModule, SharedModule],
})
export class DreModule {}
