import { NgModule } from '@angular/core';

import { ChangeAssigneeGuard } from '@aviation/request-task/guards';
import { VirTaskListComponent } from '@aviation/shared/components/vir/vir-task-list/vir-task-list.component';
import { SharedModule } from '@shared/shared.module';

import {
  CancelComponent,
  ChangeAssigneeComponent,
  ConfirmationComponent,
  RequestTaskPageComponent,
  SkipReviewComponent,
  SkipReviewConfirmationComponent,
} from './containers';
import { RequestTaskRoutingModule } from './request-task-routing.module';

@NgModule({
  imports: [RequestTaskRoutingModule, SharedModule, VirTaskListComponent],
  declarations: [
    CancelComponent,
    ChangeAssigneeComponent,
    ConfirmationComponent,
    RequestTaskPageComponent,
    SkipReviewComponent,
    SkipReviewConfirmationComponent,
  ],
  providers: [ChangeAssigneeGuard],
})
export class RequestTaskModule {}
