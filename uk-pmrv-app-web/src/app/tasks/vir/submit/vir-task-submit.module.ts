import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { VirTaskSharedModule } from '@tasks/vir/shared/vir-task-shared.module';
import { RecommendationResponseComponent } from '@tasks/vir/submit/recommendation-response/recommendation-response.component';
import { SendReportComponent } from '@tasks/vir/submit/send-report/send-report.component';
import { SubmitContainerComponent } from '@tasks/vir/submit/submit-container.component';
import { SummaryComponent } from '@tasks/vir/submit/summary/summary.component';
import { UploadEvidenceFilesComponent } from '@tasks/vir/submit/upload-evidence-files/upload-evidence-files.component';
import { UploadEvidenceQuestionComponent } from '@tasks/vir/submit/upload-evidence-question/upload-evidence-question.component';
import { VirTaskSubmitRoutingModule } from '@tasks/vir/submit/vir-task-submit-routing.module';

@NgModule({
  declarations: [
    RecommendationResponseComponent,
    SendReportComponent,
    SubmitContainerComponent,
    SummaryComponent,
    UploadEvidenceFilesComponent,
    UploadEvidenceQuestionComponent,
  ],
  imports: [SharedModule, TaskSharedModule, VirSharedModule, VirTaskSharedModule, VirTaskSubmitRoutingModule],
})
export class VirTaskSubmitModule {}
