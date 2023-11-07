import { NgModule } from '@angular/core';

import { AirSharedModule } from '@shared/air-shared/air-shared.module';
import { SharedModule } from '@shared/shared.module';
import { AirTaskSharedModule } from '@tasks/air/shared/air-task-shared.module';
import { AirTaskSubmitRoutingModule } from '@tasks/air/submit/air-task-submit-routing.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ImprovementExistingComponent } from './improvement-existing/improvement-existing.component';
import { ImprovementNegativeComponent } from './improvement-negative/improvement-negative.component';
import { ImprovementPositiveComponent } from './improvement-positive/improvement-positive.component';
import { ImprovementQuestionComponent } from './improvement-question/improvement-question.component';
import { SendReportComponent } from './send-report/send-report.component';
import { SubmitContainerComponent } from './submit-container.component';
import { SummaryComponent } from './summary/summary.component';
import { SummaryGuard } from './summary/summary.guard';

@NgModule({
  declarations: [
    ImprovementExistingComponent,
    ImprovementNegativeComponent,
    ImprovementPositiveComponent,
    ImprovementQuestionComponent,
    SendReportComponent,
    SubmitContainerComponent,
    SummaryComponent,
  ],
  imports: [AirSharedModule, AirTaskSharedModule, AirTaskSubmitRoutingModule, SharedModule, TaskSharedModule],
  providers: [SummaryGuard],
})
export class AirTaskSubmitModule {}
