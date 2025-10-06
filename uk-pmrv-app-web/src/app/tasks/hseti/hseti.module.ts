import { NgModule } from '@angular/core';

import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { HseTiService } from './core/hseti.service';
import { HseTiRoutingModule } from './hseti-routing.module';
import { SubmitContainerComponent } from './submit';

@NgModule({
  imports: [HseTiRoutingModule, SharedModule, SubmitContainerComponent, TaskSharedModule],
  providers: [HseTiService, PaymentCompletedGuard],
})
export class HseTiModule {}
