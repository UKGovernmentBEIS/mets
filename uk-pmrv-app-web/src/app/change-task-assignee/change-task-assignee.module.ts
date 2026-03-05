import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ChangeTaskAssigneeRoutingModule } from './change-task-assignee-routing.module';
import { ChangeAssigneeComponent } from './components';

@NgModule({
  declarations: [ChangeAssigneeComponent],
  imports: [ChangeTaskAssigneeRoutingModule, SharedModule],
})
export class ChangeTaskAssigneeModule {}
