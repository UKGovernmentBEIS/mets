import { NgModule } from '@angular/core';

import { RequestActionPageComponent } from '@aviation/request-action/containers';
import { VirActionTaskListComponent } from '@aviation/shared/components/vir/vir-action-task-list/vir-action-task-list.component';
import { SharedModule } from '@shared/shared.module';

import { RequestActionRoutingModule } from './request-action-routing.module';

@NgModule({
  declarations: [RequestActionPageComponent],
  imports: [RequestActionRoutingModule, SharedModule, VirActionTaskListComponent],
  providers: [],
})
export class RequestActionModule {}
