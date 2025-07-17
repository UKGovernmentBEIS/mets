import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { AlrRoutingModule } from './alr-routing.module';
import { AlrTaskSharedModule } from './shared/alr-task-shared.module';

@NgModule({
  imports: [AlrRoutingModule, AlrTaskSharedModule, SharedModule],
})
export class AlrModule {}
