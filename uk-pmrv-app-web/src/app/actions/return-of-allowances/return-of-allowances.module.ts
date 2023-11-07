import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { ActionSharedModule } from '../shared/action-shared-module';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { ReturnOfAllowancesRoutingModule } from './return-of-allowances-routing.module';
import { ReturnedComponent } from './returned/returned.component';
import { SubmittedComponent } from './submitted/submitted.component';

@NgModule({
  declarations: [PeerReviewDecisionComponent, ReturnedComponent, SubmittedComponent],
  imports: [ActionSharedModule, ReturnOfAllowancesRoutingModule, SharedModule],
})
export class ReturnOfAllowancesModule {}
