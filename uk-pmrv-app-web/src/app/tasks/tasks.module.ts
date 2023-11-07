import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { TaskComponent } from './task.component';
import { TasksRoutingModule } from './tasks-routing.module';

@NgModule({
  declarations: [TaskComponent],
  imports: [SharedModule, TasksRoutingModule],
})
export class TasksModule {}
