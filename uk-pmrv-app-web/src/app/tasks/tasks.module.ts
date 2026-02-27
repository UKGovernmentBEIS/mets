import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { InspectionItemResolver } from './inspection/shared/resolvers/follow-up-action.resolver';
import { TaskComponent } from './task.component';
import { TasksRoutingModule } from './tasks-routing.module';

@NgModule({
  imports: [SharedModule, TasksRoutingModule],
  declarations: [TaskComponent],
  providers: [InspectionItemResolver],
})
export class TasksModule {}
