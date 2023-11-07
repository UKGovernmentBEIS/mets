import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { ResponseItemResolver } from '@tasks/vir/shared/resolvers/response-item.resolver';
import { RecommendationResponseComponent } from '@tasks/vir/submit/recommendation-response/recommendation-response.component';
import { SendReportComponent } from '@tasks/vir/submit/send-report/send-report.component';
import { SubmitContainerComponent } from '@tasks/vir/submit/submit-container.component';
import { SummaryComponent } from '@tasks/vir/submit/summary/summary.component';
import { SummaryGuard } from '@tasks/vir/submit/summary/summary.guard';
import { UploadEvidenceFilesComponent } from '@tasks/vir/submit/upload-evidence-files/upload-evidence-files.component';
import { UploadEvidenceQuestionComponent } from '@tasks/vir/submit/upload-evidence-question/upload-evidence-question.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { pageTitle: 'Verifier improvement report submit' },
        component: SubmitContainerComponent,
      },
      {
        path: 'send-report',
        data: { pageTitle: 'Send report' },
        component: SendReportComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: ':id',
        resolve: { verificationDataItem: ResponseItemResolver },
        children: [
          {
            path: 'recommendation-response',
            data: { pageTitle: 'Respond to an item' },
            component: RecommendationResponseComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'upload-evidence-question',
            data: { pageTitle: 'Upload evidence question' },
            component: UploadEvidenceQuestionComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'upload-evidence-files',
            data: { pageTitle: 'Upload evidence files' },
            component: UploadEvidenceFilesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers' },
            component: SummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VirTaskSubmitRoutingModule {}
