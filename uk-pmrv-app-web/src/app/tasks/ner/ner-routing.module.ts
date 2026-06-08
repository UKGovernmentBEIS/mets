import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PaymentNotCompletedComponent } from '@aviation/request-task/containers';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';
import { tasksReturnToOperatorGuard } from '@tasks/shared/guards';

import { tasksReturnToOperatorFormProvider } from '../shared/components';
import { nerSendReportGuard, wizardStepGuard } from './core';
import { NerReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { NerSendReportQuestionComponent } from './shared/components/send-report/question/question.component';
import { NerChangesRequestedComponent } from './submit/changes-requested/changes-requested.component';
import { changingResolver, nerSendReportBacklinkResolver, nerSendReportTitleResolver } from './utils';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete new entrant reserve' },
        loadComponent: () => import('./shared').then((c) => c.NerTaskListComponent),
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload new entrant reserve' },
            loadComponent: () => import('./submit').then((c) => c.NerDetailsUploadNerComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'upload-mmp',
            data: { pageTitle: 'Upload monitoring methodology plan', backlink: '../' },
            loadComponent: () => import('./submit').then((c) => c.NerDetailsUploadMmpComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'New entrant reserve - Summary' },
            loadComponent: () => import('./shared').then((c) => c.NerDetailsSummaryComponent),
            canActivate: [wizardStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: NerChangesRequestedComponent,
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            data: { pageTitle: 'Send application for verification' },
            resolve: {
              pageTitle: nerSendReportTitleResolver,
              backlink: nerSendReportBacklinkResolver,
            },
            canActivate: [nerSendReportGuard],
            loadComponent: () => import('./shared').then((c) => c.NerSendReportComponent),
          },
          {
            path: 'question',
            data: { pageTitle: 'Submit your application' },
            component: NerSendReportQuestionComponent,
          },
        ],
      },
      {
        path: 'recall-from-verifier',
        data: { pageTitle: 'Recall the application', breadcrumb: true },
        loadComponent: () =>
          import('../../shared/components/recall/recall.component').then((c) => c.RecallSharedComponent),
      },
    ],
  },
  {
    path: 'verification-submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Verify new entrant reserve' },
        loadComponent: () => import('./shared').then((c) => c.NerTaskListComponent),
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        data: { pageTitle: 'New entrant reserve', breadcrumb: true },
        loadComponent: () => import('./shared').then((c) => c.NerDetailsSummaryComponent),
      },
      {
        path: 'opinion-statement',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload new entrant reserve verification opinion statement' },
            loadComponent: () => import('./verification-submit').then((c) => c.NerUploadOpinionStatementComponent),
            canActivate: [wizardStepGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Opinion statement summary', breadcrumb: true },
            loadComponent: () => import('./shared').then((c) => c.NerOpinionStatementSummaryComponent),
            canActivate: [wizardStepGuard],
          },
        ],
      },
      {
        path: 'overall-decision',
        children: [
          {
            path: '',
            data: { pageTitle: 'What is your assessment of this report?' },
            loadComponent: () => import('./verification-submit').then((c) => c.NerOverallDecisionAssessmentComponent),
            canActivate: [wizardStepGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Overall decision - Summary' },
            canActivate: [wizardStepGuard],
            loadComponent: () => import('./shared').then((c) => c.NerOverallDecisionSummaryComponent),
          },
        ],
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            data: { pageTitle: 'Send verification report to the operator' },
            loadComponent: () => import('./shared').then((c) => c.NerSendReportComponent),
          },
        ],
      },
      {
        path: 'return-to-operator-for-changes',
        providers: [tasksReturnToOperatorFormProvider],
        children: [
          {
            path: '',
            data: { pageTitle: 'Changes required by the operator' },
            loadComponent: () => import('../shared/components').then((c) => c.TasksReturnToOperatorComponent),
          },
          {
            path: 'summary',
            canActivate: [tasksReturnToOperatorGuard],
            data: { pageTitle: 'Check your answers', breadcrumb: true },
            loadComponent: () => import('../shared/components').then((c) => c.TasksReturnToOperatorSummaryComponent),
          },
        ],
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review new entrant reserve' },
        loadComponent: () => import('./shared').then((c) => c.NerTaskListComponent),
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        data: { pageTitle: 'Review the new entrant reserve details', groupKey: 'NER', breadcrumb: true },
        loadComponent: () => import('./shared').then((c) => c.NerDetailsSummaryComponent),
      },
      {
        path: 'opinion-statement',
        data: {
          pageTitle: 'Review the new entrant reserve verification opinion statement',
          groupKey: 'OPINION_STATEMENT',
          breadcrumb: true,
        },
        loadComponent: () => import('./shared').then((c) => c.NerOpinionStatementSummaryComponent),
      },
      {
        path: 'overall-decision',
        data: {
          pageTitle: 'Review the overall decision',
          groupKey: 'OVERALL_DECISION',
          breadcrumb: true,
        },
        loadComponent: () => import('./shared').then((c) => c.NerOverallDecisionSummaryComponent),
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amendments', breadcrumb: true },
            component: NerReturnForAmendsComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'outcome',
        children: [
          {
            path: '',
            resolve: {
              changing: changingResolver,
            },
            data: { pageTitle: 'What is your decision on the new entrant reserve application?' },
            canActivate: [wizardStepGuard],
            loadComponent: () => import('./shared').then((c) => c.NerDetailsSummaryComponent),
          },
          {
            path: 'upload-ner',
            data: { pageTitle: 'Upload new entrant reserve', backlink: '../' },
            loadComponent: () => import('./review').then((c) => c.NerReviewUploadNerComponent),
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', isSummary: true, breadcrumb: 'Outcome - Summary' },
            canActivate: [wizardStepGuard],
            loadComponent: () => import('./shared').then((c) => c.NerDetailsSummaryComponent),
          },
        ],
      },
      {
        path: 'send-for-peer-review',
        data: { pageTitle: 'Send for peer review', breadcrumb: true },
        component: PeerReviewComponent,
        canActivate: [PaymentCompletedGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'payment-not-completed',
        data: { pageTitle: 'Payment no completed', backlink: '../' },
        component: PaymentNotCompletedComponent,
      },
      {
        path: 'peer-review-decision',
        providers: [PeerReviewDecisionGuard],
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review decision' },
            component: PeerReviewDecisionComponent,
            canActivate: [PeerReviewDecisionGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Peer review decision answers', breadcrumb: 'Summary' },
            component: PeerReviewDecisionAnswersComponent,
            canActivate: [PeerReviewDecisionAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            data: { pageTitle: 'Peer review decision confirmation' },
            component: PeerReviewDecisionConfirmationComponent,
          },
        ],
      },
      {
        path: 'complete-withdraw',
        data: { pageTitle: 'Complete or withdraw application' },
        canActivate: [PaymentCompletedGuard],
        loadComponent: () => import('./review').then((c) => c.NerCompleteWithdrawComponent),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NerRoutingModule {}
