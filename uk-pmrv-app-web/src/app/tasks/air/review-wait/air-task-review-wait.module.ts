import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { ReviewWaitComponent } from '@tasks/air/review-wait/review-wait.component';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { AirTaskReviewWaitRoutingModule } from './air-task-review-wait-routing.module';

@NgModule({
  declarations: [ReviewWaitComponent],
  imports: [AirTaskReviewWaitRoutingModule, AirTaskSharedModule, SharedModule, TaskSharedModule],
})
export class AirTaskReviewWaitModule {}
