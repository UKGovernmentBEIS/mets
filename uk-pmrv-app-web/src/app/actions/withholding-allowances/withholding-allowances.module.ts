import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../shared/action-shared-module';
import { ClosedComponent } from './closed/closed.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { SubmittedWithdrawnComponent } from './submitted-withdrawn/submitted-withdrawn.component';
import { WithholdingAllowancesRoutingModule } from './withholding-allowances-routing.module';

@NgModule({
  declarations: [ClosedComponent, PeerReviewDecisionComponent, SubmittedWithdrawnComponent],
  imports: [ActionSharedModule, SharedModule, WithholdingAllowancesRoutingModule],
})
export class WithholdingAllowancesModule {}
