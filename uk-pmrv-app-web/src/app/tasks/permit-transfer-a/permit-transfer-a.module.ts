import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { PermitTransferARoutingModule } from './permit-transfer-a-routing.module';
import { TransferAAemReportComponent } from './submit/aem-report/aem-report.component';
import { TransferACodeComponent } from './submit/code/code.component';
import { TransferADateComponent } from './submit/date/date.component';
import { TransferAPaymentComponent } from './submit/payment/payment.component';
import { TransferAReasonComponent } from './submit/reason/reason.component';
import { SendApplicationComponent } from './submit/send-application/send-application.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { TransferWaitComponent } from './wait/wait.component';

@NgModule({
  declarations: [
    SendApplicationComponent,
    SubmitContainerComponent,
    SummaryComponent,
    TransferAAemReportComponent,
    TransferACodeComponent,
    TransferADateComponent,
    TransferAPaymentComponent,
    TransferAReasonComponent,
    TransferWaitComponent,
  ],
  imports: [PermitTransferARoutingModule, SharedModule, TaskSharedModule],
})
export class PermitTransferAModule {}
