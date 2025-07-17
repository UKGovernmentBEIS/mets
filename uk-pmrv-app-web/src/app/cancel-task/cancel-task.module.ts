import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { CancelTaskRoutingModule } from './cancel-task-routing.module';
import { CancelComponent, ConfirmationComponent } from './components';

@NgModule({
  declarations: [CancelComponent, ConfirmationComponent],
  imports: [CancelTaskRoutingModule, SharedModule],
})
export class CancelTaskModule {}
