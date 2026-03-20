import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { CancelTaskRoutingModule } from './cancel-task-routing.module';
import { CancelComponent, ConfirmationComponent } from './components';

@NgModule({
  imports: [CancelTaskRoutingModule, SharedModule],
  declarations: [CancelComponent, ConfirmationComponent],
})
export class CancelTaskModule {}
