import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { ActionSharedModule } from '../shared/action-shared-module';
import { CompletedComponent } from './completed/completed.component';
import { PermitBatchReissueRoutingModule } from './permit-batch-reissue-actions-routing.module';
import { SubmittedComponent } from './submitted/submitted.component';

@NgModule({
  declarations: [CompletedComponent, SubmittedComponent],
  imports: [ActionSharedModule, PermitBatchReissueRoutingModule, SharedModule, SharedUserModule],
})
export class PermitBatchReissueActionsModule {}
