import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { WasteQdrRoutingModule } from './waste-qdr-routing.module';

@NgModule({
  imports: [SharedModule, WasteQdrRoutingModule],
})
export class WasteQdrModule {}
