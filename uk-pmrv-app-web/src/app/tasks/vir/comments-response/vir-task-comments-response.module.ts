import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';

import { CommentsResponseContainerComponent } from './comments-response-container.component';
import { OperatorFollowupComponent } from './operator-followup/operator-followup.component';
import { SendReportComponent } from './send-report/send-report.component';
import { SummaryComponent } from './summary/summary.component';
import { VirTaskCommentsResponseRoutingModule } from './vir-task-comments-response-routing.module';

@NgModule({
  declarations: [CommentsResponseContainerComponent, OperatorFollowupComponent, SendReportComponent, SummaryComponent],
  imports: [SharedModule, TaskSharedModule, VirSharedModule, VirTaskCommentsResponseRoutingModule, VirTaskSharedModule],
})
export class VirTaskCommentsResponseModule {}
