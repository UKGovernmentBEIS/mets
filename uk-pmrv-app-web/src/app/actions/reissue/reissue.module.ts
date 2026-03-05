import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { ActionSharedModule } from '../shared/action-shared-module';
import { CompletedComponent } from './completed/completed.component';
import { ReissueRoutingModule } from './reissue-routing.module';

@NgModule({
  declarations: [CompletedComponent],
  imports: [ActionSharedModule, ReissueRoutingModule, SharedModule, SharedUserModule],
})
export class ReissueModule {}
