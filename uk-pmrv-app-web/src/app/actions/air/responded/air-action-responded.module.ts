import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { AirActionRespondedRoutingModule } from './air-action-responded-routing.module';
import { RespondedComponent } from './responded.component';

@NgModule({
  imports: [ActionSharedModule, AirActionRespondedRoutingModule, AirSharedModule, SharedModule],
  declarations: [RespondedComponent],
})
export class AirActionRespondedModule {}
