import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { SharedModule } from '@shared/shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { AirTaskCommentsResponseRoutingModule } from './air-task-comments-response-routing.module';
import { CommentsResponseContainerComponent } from './comments-response-container.component';
import { OperatorFollowupComponent } from './operator-followup/operator-followup.component';
import { SendReportComponent } from './send-report/send-report.component';
import { SummaryComponent } from './summary/summary.component';
import { SummaryGuard } from './summary/summary.guard';

@NgModule({
  declarations: [CommentsResponseContainerComponent, OperatorFollowupComponent, SendReportComponent, SummaryComponent],
  imports: [AirSharedModule, AirTaskCommentsResponseRoutingModule, AirTaskSharedModule, SharedModule, TaskSharedModule],
  providers: [SummaryGuard],
})
export class AirTaskCommentsResponseModule {}
