import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ReviewNotificationStatusPipe } from '@permit-notification/shared/pipes/review-notification-status.pipe';
import { SectionStatusPipe } from '@permit-notification/shared/pipes/section-status.pipe';
import { SharedModule } from '@shared/shared.module';

import { BaseTaskContainerComponent, ReturnLinkComponent, TaskLayoutComponent } from './components';

@NgModule({
  imports: [RouterModule, SharedModule],
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
})
export class TaskSharedModule {}
