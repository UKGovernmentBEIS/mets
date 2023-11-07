import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ReviewNotificationStatusPipe } from '@permit-notification/shared/pipes/review-notification-status.pipe';
import { SectionStatusPipe } from '@permit-notification/shared/pipes/section-status.pipe';
import { SharedModule } from '@shared/shared.module';

import { BaseTaskContainerComponent } from './components/base-task-container-component/base-task-container.component';
import { ReturnLinkComponent } from './components/return-link/return-link.component';
import { TaskLayoutComponent } from './components/task-layout/task-layout.component';

@NgModule({
  declarations: [
    BaseTaskContainerComponent,
    ReturnLinkComponent,
    ReviewNotificationStatusPipe,
    SectionStatusPipe,
    TaskLayoutComponent,
  ],
  exports: [
    BaseTaskContainerComponent,
    ReturnLinkComponent,
    ReviewNotificationStatusPipe,
    SectionStatusPipe,
    TaskLayoutComponent,
  ],
  imports: [RouterModule, SharedModule],
})
export class TaskSharedModule {}
