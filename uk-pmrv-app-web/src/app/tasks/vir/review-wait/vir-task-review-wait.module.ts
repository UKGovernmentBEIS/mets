import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { ReviewWaitComponent } from '@tasks/vir/review-wait/review-wait.component';

import { VirTaskReviewWaitRoutingModule } from './vir-task-review-wait-routing.module';

@NgModule({
  declarations: [ReviewWaitComponent],
  imports: [SharedModule, TaskSharedModule, VirTaskReviewWaitRoutingModule],
})
export class VirTaskReviewWaitModule {}
