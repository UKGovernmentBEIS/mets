import { NgModule } from '@angular/core';

import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { SharedModule } from '@shared/shared.module';

import { NerService } from './core';
import { NerRoutingModule } from './ner-routing.module';

@NgModule({
  imports: [SharedModule, NerRoutingModule],
  providers: [NerService, CapitalizeFirstPipe, PaymentCompletedGuard],
})
export class NerModule {}
