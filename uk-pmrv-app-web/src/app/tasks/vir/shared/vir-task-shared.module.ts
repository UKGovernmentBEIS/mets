import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { VerificationDataGroupComponent } from '@tasks/vir/shared/components/verification-data-group/verification-data-group.component';
import { VerificationDataGroupReviewComponent } from '@tasks/vir/shared/components/verification-data-group-review/verification-data-group-review.component';
import { VirTaskComponent } from '@tasks/vir/shared/components/vir-task/vir-task.component';
import { SubmitRespondStatusPipe } from '@tasks/vir/shared/pipes/submit-respond-status.pipe';
import { TaskStatusPipe } from '@tasks/vir/shared/pipes/task-status.pipe';

@NgModule({
  declarations: [
    SubmitRespondStatusPipe,
    TaskStatusPipe,
    VerificationDataGroupComponent,
    VerificationDataGroupReviewComponent,
    VirTaskComponent,
  ],
  exports: [
    SubmitRespondStatusPipe,
    TaskStatusPipe,
    VerificationDataGroupComponent,
    VerificationDataGroupReviewComponent,
    VirTaskComponent,
  ],
  imports: [RouterModule, SharedModule, VirSharedModule],
})
export class VirTaskSharedModule {}
