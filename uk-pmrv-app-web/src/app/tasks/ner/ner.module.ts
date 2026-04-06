import { NgModule } from '@angular/core';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { SharedModule } from '@shared/shared.module';

import { NerService } from './core';
import { NerRoutingModule } from './ner-routing.module';

@NgModule({
  imports: [SharedModule, NerRoutingModule],
  providers: [NerService, CapitalizeFirstPipe],
})
export class NerModule {}
