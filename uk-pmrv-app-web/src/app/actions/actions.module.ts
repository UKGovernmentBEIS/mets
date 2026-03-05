import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { ActionComponent } from './action.component';
import { ActionsRoutingModule } from './actions-routing.module';
import { SubmittedComponent } from './permit-transfer-a/submitted/submitted.component';
import { ActionSharedModule } from './shared/action-shared-module';

@NgModule({
  declarations: [ActionComponent, SubmittedComponent],
  imports: [ActionSharedModule, ActionsRoutingModule, SharedModule, VirSharedModule],
})
export class ActionsModule {}
