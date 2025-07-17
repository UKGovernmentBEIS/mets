import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RecallSharedComponent } from '@shared/components/recall/recall.component';

import { AlrSendReportComponent } from './shared';
import {
  ActivitySummaryGuard,
  AlrSendReportVerifierGuard,
  AlrTaskListComponent,
  ALRUploadReportComponent,
  SummaryComponent,
} from './submit';
import {
  ActivityVerifierReviewComponent,
  AlrOpinionStatementSummaryComponent,
  AlrUploadOpinionStatementComponent,
  OverallDecisionAssessmentComponent,
  OverallDecisionSummaryComponent,
  VerificationSubmitTaskListComponent,
} from './verification-submit';
import { opinionStatementSummaryGuard, overallDecisionSummaryGuard } from './verification-submit/guards';
import { returnToOperatorGuard } from './verification-submit/guards/return-to-operator.guard';
import { AlrReturnToOperatorComponent, returnToOperatorFormProvider } from './verification-submit/return-to-operator';
import { AlrReturnToOperatorSummaryComponent } from './verification-submit/return-to-operator/return-to-operator-summary/return-to-operator-summary.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete activity level report' },
        component: AlrTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'activity',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload the activity level report file' },
            component: ALRUploadReportComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
              breadcrumb: 'Summary',
            },
            component: SummaryComponent,
            providers: [ActivitySummaryGuard],
            canActivate: [ActivitySummaryGuard],
          },
        ],
      },
      {
        path: 'send-report-verifier',
        data: { pageTitle: 'Send report for verification' },
        component: AlrSendReportComponent,
        providers: [AlrSendReportVerifierGuard],
        canActivate: [AlrSendReportVerifierGuard],
      },
      {
        path: 'recall-from-verifier',
        data: { pageTitle: 'Recall the report', breadcrumb: true },
        component: RecallSharedComponent,
      },
    ],
  },
  {
    path: 'verification-submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Verify activity level report' },
        component: VerificationSubmitTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'Activity level report', breadcrumb: 'Summary' },
        component: ActivityVerifierReviewComponent,
      },
      {
        path: 'overall-decision',
        children: [
          {
            path: '',
            data: { pageTitle: 'What is your assessment of this report?' },
            component: OverallDecisionAssessmentComponent,
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: true },
            canActivate: [overallDecisionSummaryGuard],
            component: OverallDecisionSummaryComponent,
          },
        ],
      },
      {
        path: 'opinion-statement',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload the activity level report verification opinion statement' },
            component: AlrUploadOpinionStatementComponent,
          },
          {
            path: 'summary',
            data: { pageTitle: 'Opinion statement summary', breadcrumb: true },
            canActivate: [opinionStatementSummaryGuard],
            component: AlrOpinionStatementSummaryComponent,
          },
        ],
      },
      {
        path: 'send-report-to-operator',
        data: { pageTitle: 'Send verification report to the operator' },
        component: AlrSendReportComponent,
      },
      {
        path: 'return-to-operator-for-changes',
        providers: [returnToOperatorFormProvider],
        children: [
          {
            path: '',
            component: AlrReturnToOperatorComponent,
          },
          {
            path: 'summary',
            canActivate: [returnToOperatorGuard],
            data: { pageTitle: 'Check your answers', breadcrumb: true },
            component: AlrReturnToOperatorSummaryComponent,
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
export class AlrRoutingModule {}
