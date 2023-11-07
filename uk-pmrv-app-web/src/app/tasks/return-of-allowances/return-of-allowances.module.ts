import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { SharedModule } from '../../shared/shared.module';
import { TaskSharedModule } from '../shared/task-shared-module';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { PeerReviewComponent } from './peer-review/peer-review.component';
import { SummaryDetailsComponent } from './peer-review/summary-details/summary-details.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { ReturnOfAllowancesRoutingModule } from './return-of-allowances-routing.module';
import { ConfirmationComponent } from './returned-allowances/confirmation/confirmation.component';
import { ProvideReturnedDetailsComponent } from './returned-allowances/provide-returned-details/provide-returned-details.component';
import { ReturnedAllowancesComponent } from './returned-allowances/returned-allowances.component';
import { SummaryComponent as ReturnedSummaryComponent } from './returned-allowances/summary/summary.component';
import { ProvideDetailsComponent } from './submit/provide-details/provide-details.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';

@NgModule({
  declarations: [
    ConfirmationComponent,
    NotifyOperatorComponent,
    PeerReviewComponent,
    PeerReviewWaitComponent,
    ProvideDetailsComponent,
    ProvideReturnedDetailsComponent,
    ReturnedAllowancesComponent,
    ReturnedSummaryComponent,
    SubmitContainerComponent,
    SummaryComponent,
    SummaryDetailsComponent,
  ],
  imports: [CommonModule, ReturnOfAllowancesRoutingModule, SharedModule, TaskSharedModule],
  providers: [PeerReviewDecisionGuard],
})
export class ReturnOfAllowancesModule {}
