import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DetailsComponent, FreeAllocationComponent, SubmitComponent } from './submit';
import { CBAMComponent } from './submit/cbam/cbam.component';
import { BDRS2BaselineStepGuard } from './submit/guards/baseline-step-guard';
import { BDRS2BaselineSummaryGuard } from './submit/guards/baseline-summary-guard';
import { Bdrs2SendReportGuard } from './submit/guards/send-report-guard';
import { Bdrs2SendReportVerifierGuard } from './submit/guards/send-report-verifier-guard';
import { Bdrs2SendReportConfirmationComponent } from './submit/send-report/confirmation/confirmation.component';
import { Bdrs2SendReportRegulatorComponent } from './submit/send-report/send-report-regulator/send-report-regulator.component';
import { Bdrs2SendReportVerifierComponent } from './submit/send-report/send-report-verifier/send-report-verifier.component';
import { BdrS2SendVerifierOrRegulatorComponent } from './submit/send-report/send-verifier-or-regulator/send-verifier-or-regulator.component';
import { BDRS2SummaryComponent } from './submit/summary/summary.component';
import { BDRS2UploadMmpComponent } from './submit/upload-mmp/bdrs2-upload-mmp.component';
import { BDRS2UploadReportComponent } from './submit/upload-report/bdrs2-upload-report.component';
import { bdrs2SendReportBacklinkResolver, bdrs2UploadFileBacklinkResolver } from './utils';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Stage 2 baseline data report`;' },
        component: SubmitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'baseline',
        children: [
          {
            path: '',
            canActivate: [BDRS2BaselineStepGuard],
            data: { pageTitle: 'Do you want to continue with your application for free allocation?' },
            component: FreeAllocationComponent,
          },
          {
            path: 'details',
            canActivate: [BDRS2BaselineStepGuard],
            data: { pageTitle: 'Free allocation and emitter status', backlink: '../' },
            component: DetailsComponent,
          },
          {
            path: 'cbam',
            canActivate: [BDRS2BaselineStepGuard],
            data: { pageTitle: 'UK Carbon Border Adjustment Mechanism (CBAM)', backlink: '../details' },
            component: CBAMComponent,
          },
          {
            path: 'upload-report',
            resolve: { backlink: bdrs2UploadFileBacklinkResolver },
            canActivate: [BDRS2BaselineStepGuard],
            data: { pageTitle: 'Upload stage 2 baseline data report' },
            component: BDRS2UploadReportComponent,
          },
          {
            path: 'upload-mmp',
            data: { pageTitle: 'Upload monitoring methodology plan', backlink: '../upload-report' },
            canActivate: [BDRS2BaselineStepGuard],
            component: BDRS2UploadMmpComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
            },
            canActivate: [BDRS2BaselineSummaryGuard],
            component: BDRS2SummaryComponent,
          },
        ],
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            data: { pageTitle: 'Submit your report' },
            canActivate: [Bdrs2SendReportGuard],
            component: BdrS2SendVerifierOrRegulatorComponent,
          },
          {
            path: 'regulator',
            data: { pageTitle: 'Send to regulator', backlink: ({ backlinkUrl }) => backlinkUrl },
            resolve: { backlinkUrl: bdrs2SendReportBacklinkResolver },
            component: Bdrs2SendReportRegulatorComponent,
          },
          {
            path: 'verifier',
            data: { pageTitle: 'Send report for verification', backlink: ({ backlinkUrl }) => backlinkUrl },
            resolve: { backlinkUrl: bdrs2SendReportBacklinkResolver },
            canActivate: [Bdrs2SendReportVerifierGuard],
            component: Bdrs2SendReportVerifierComponent,
          },
          {
            path: 'confirmation',
            component: Bdrs2SendReportConfirmationComponent,
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
export class BdrS2RoutingModule {}
