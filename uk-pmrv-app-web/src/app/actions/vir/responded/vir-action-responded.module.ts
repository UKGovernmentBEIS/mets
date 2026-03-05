import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { RespondedComponent } from './responded.component';
import { VirActionRespondedRoutingModule } from './vir-action-responded-routing.module';

@NgModule({
  declarations: [RespondedComponent],
  imports: [ActionSharedModule, SharedModule, VirActionRespondedRoutingModule, VirSharedModule],
})
export class VirActionRespondedModule {}
