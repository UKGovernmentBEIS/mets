import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { InspectionItemResolver } from './inspection/shared/resolvers/follow-up-action.resolver';
import { TaskComponent } from './task.component';
import { TasksRoutingModule } from './tasks-routing.module';

@NgModule({
  declarations: [TaskComponent],
  imports: [SharedModule, TasksRoutingModule],
  providers: [InspectionItemResolver],
})
export class TasksModule {}
