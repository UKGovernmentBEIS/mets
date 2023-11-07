import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewComponent } from '@tasks/withholding-allowances/peer-review/peer-review.component';
import { PeerReviewWaitComponent } from '@tasks/withholding-allowances/peer-review-wait/peer-review-wait.component';

import { SharedModule } from '../../shared/shared.module';
import { TaskSharedModule } from '../shared/task-shared-module';
import { SummaryDetailsComponent } from './shared/components/summary-details/summary-details.component';
import { WithholdingAllowancesTaskComponent } from './shared/components/withholding-allowances-task/withholding-allowances-task.component';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { RecoveryDetailsComponent } from './submit/recovery-details/recovery-details.component';
import { SubmitComponent } from './submit/submit.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { WithdrawComponent } from './withdraw/withdraw.component';
import { CloseConfirmationComponent } from './withdraw/withdraw-close/close-confirmation/close-confirmation.component';
import { WithdrawCloseComponent } from './withdraw/withdraw-close/withdraw-close.component';
import { WithdrawNotifyOperatorComponent } from './withdraw/withdraw-notify-operator/withdraw-notify-operator.component';
import { WithdrawReasonComponent } from './withdraw/withdraw-reason/withdraw-reason.component';
import { WithdrawSummaryComponent } from './withdraw/withdraw-summary/withdraw-summary.component';
import { WithholdingAllowancesRoutingModule } from './withholding-allowances-routing.module';

@NgModule({
  declarations: [
    CloseConfirmationComponent,
    NotifyOperatorComponent,
    PeerReviewComponent,
    PeerReviewWaitComponent,
    RecoveryDetailsComponent,
    SubmitComponent,
    SummaryComponent,
    SummaryDetailsComponent,
    WithdrawCloseComponent,
    WithdrawComponent,
    WithdrawNotifyOperatorComponent,
    WithdrawReasonComponent,
    WithdrawSummaryComponent,
    WithholdingAllowancesTaskComponent,
  ],
  imports: [CommonModule, SharedModule, TaskSharedModule, WithholdingAllowancesRoutingModule],
  providers: [PeerReviewDecisionGuard],
})
export class WithholdingAllowancesModule {}
