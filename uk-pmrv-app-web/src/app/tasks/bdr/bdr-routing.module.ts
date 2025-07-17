import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { RecallSharedComponent } from '@shared/components/recall/recall.component';
import { BdrOpinionStatementReviewComponent } from '@tasks/bdr/review/opinion-statement/opinion-statement-review.component';
import { BdrOverallDecisionReviewComponent } from '@tasks/bdr/review/overall-decision/overall-decision-review.component';
import {
  OPINION_STATEMENT,
  OVERALL_DECISION,
} from '@tasks/bdr/shared/components/decision/bdr-review-decision/bdr-verification-review-group-decision.form.util';
import {
  OverallDecisionAssessmentComponent,
  OverallDecisionSummaryComponent,
} from '@tasks/bdr/verification-submit/overall-decision';

import {
  BaselineReviewComponent,
  BdrCompleteConfirmationComponent,
  BdrCompleteReviewComponent,
  Outcome27aQuestionComponent,
  OutcomeFaDecisionComponent,
  OutcomeSummaryComponent,
  OutcomeUploadFilesComponent,
  OutcomeUseHseDecisionComponent,
  ReturnForAmendsComponent,
  ReviewContainerComponent,
} from './review';
import { BdrCompleteReviewGuard } from './review/guards/bdr-complete-review.guard';
import { outcomeSummaryGuard } from './review/outcome/outcome-summary.guard';
import { outcomeReviewBacklinkResolver } from './review/review.wizard';
import {
  BaselineStepGuard,
  BaselineSummaryGuard,
  BdrSendReportConfirmationComponent,
  FreeAllocationComponent,
  ProvideMmpComponent,
  SendReportGuard,
  SendReportRegulatorComponent,
  SendReportVerifierComponent,
  SendReportVerifierGuard,
  SendVerifierOrRegulatorComponent,
  SubmitContainerComponent,
  SummaryComponent,
  UploadReportComponent,
} from './submit';
import { BdrChangesRequestedComponent } from './submit/changes-requested/bdr-changes-requested.component';
import { bdrSendReportBacklinkResolver } from './submit/submit.wizard';
import {
  BaselineVerifierReviewComponent,
  BdrReturnToOperatorComponent,
  BdrReturnToOperatorSummaryComponent,
  OpinionStatementSummaryComponent,
  OpinionStatementSummaryGuard,
  overallDecisionSummaryGuard,
  returnToOperatorFormProvider,
  returnToOperatorGuard,
  SendBdrReportComponent,
  UploadOpinionStatementComponent,
  VerificationSubmitContainerComponent,
} from './verification-submit';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Baseline data report' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            data: { pageTitle: 'Submit your report' },
            canActivate: [SendReportGuard],
            component: SendVerifierOrRegulatorComponent,
          },
          {
            path: 'regulator',
            data: { pageTitle: 'Send to regulator', backlink: ({ backlinkUrl }) => backlinkUrl },
            resolve: { backlinkUrl: bdrSendReportBacklinkResolver },
            component: SendReportRegulatorComponent,
          },
          {
            path: 'verifier',
            data: { pageTitle: 'Send report for verification', backlink: ({ backlinkUrl }) => backlinkUrl },
            resolve: { backlinkUrl: bdrSendReportBacklinkResolver },
            canActivate: [SendReportVerifierGuard],
            component: SendReportVerifierComponent,
          },
          {
            path: 'confirmation',
            component: BdrSendReportConfirmationComponent,
          },
        ],
      },
      {
        path: 'baseline',
        children: [
          {
            path: '',
            data: { pageTitle: 'Upload the baseline data report file' },
            canActivate: [BaselineStepGuard],
            component: UploadReportComponent,
          },
          {
            path: 'free-allocation',
            data: { pageTitle: 'Free allocation and emitter status', backlink: '../' },
            canActivate: [BaselineStepGuard],
            component: FreeAllocationComponent,
          },
          {
            path: 'provide-mmp',
            data: {
              pageTitle: 'Are you providing a monitoring methodology plan as part of your application?',
              backlink: '../free-allocation',
            },
            canActivate: [BaselineStepGuard],
            component: ProvideMmpComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
            },
            canActivate: [BaselineSummaryGuard],
            component: SummaryComponent,
          },
        ],
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: BdrChangesRequestedComponent,
      },
      {
        path: 'recall-bdr-from-verifier',
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
        data: { pageTitle: 'Baseline data report' },
        component: VerificationSubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'return-to-operator-for-changes',
        providers: [returnToOperatorFormProvider],
        children: [
          {
            path: '',
            component: BdrReturnToOperatorComponent,
          },
          {
            path: 'summary',
            canActivate: [returnToOperatorGuard],
            data: { pageTitle: 'Check your answers', breadcrumb: true },
            component: BdrReturnToOperatorSummaryComponent,
          },
        ],
      },
      {
        path: 'baseline',
        component: BaselineVerifierReviewComponent,
      },
      {
        path: 'opinion-statement',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Upload the BDR verification opinion statement',
            },
            component: UploadOpinionStatementComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
            },
            canActivate: [OpinionStatementSummaryGuard],
            component: OpinionStatementSummaryComponent,
          },
        ],
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
        path: 'send-report',
        data: {
          pageTitle: 'Send report to operator',
        },
        component: SendBdrReportComponent,
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review baseline data report' },
        component: ReviewContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'complete-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Complete review', breadcrumb: true, backlink: '../' },
            component: BdrCompleteReviewComponent,
            canActivate: [BdrCompleteReviewGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            component: BdrCompleteConfirmationComponent,
          },
        ],
      },
      {
        path: 'baseline',
        data: { pageTitle: 'Baseline data report and details', groupKey: 'BDR', breadcrumb: true },

        component: BaselineReviewComponent,
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'BDR verification opinion statement', groupKey: OPINION_STATEMENT, breadcrumb: true },

        component: BdrOpinionStatementReviewComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'BDR overall decision', groupKey: OVERALL_DECISION, breadcrumb: true },

        component: BdrOverallDecisionReviewComponent,
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amends', breadcrumb: true },
            component: ReturnForAmendsComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'outcome',
        children: [
          {
            path: 'fa-decision',
            data: {
              pageTitle: 'What is your decision for the free allocation? - Outcome of regulator review',
            },
            component: OutcomeFaDecisionComponent,
          },
          {
            path: 'use-hse-decision',
            data: {
              pageTitle: 'What is your decision on the USE/HSE application? - Outcome of regulator review',
              backlink: '../fa-decision',
            },
            component: OutcomeUseHseDecisionComponent,
          },
          {
            path: '27a-question',
            data: {
              pageTitle: 'Has the operator met the 27A data submission requirements? - Outcome of regulator review',
              backlink: '../use-hse-decision',
            },
            component: Outcome27aQuestionComponent,
          },
          {
            path: 'upload-files',
            data: {
              pageTitle: 'Upload the baseline data report file - Outcome of regulator review',
              backlink: ({ backlinkUrl }) => backlinkUrl,
            },
            resolve: { backlinkUrl: outcomeReviewBacklinkResolver },
            component: OutcomeUploadFilesComponent,
          },
          {
            path: '',
            data: {
              pageTitle: 'Check your answers - Outcome of regulator review',
              backlink: './upload-files',
            },
            canActivate: [outcomeSummaryGuard],
            component: OutcomeSummaryComponent,
          },
        ],
      },
      {
        path: 'peer-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Send for peer review', breadcrumb: true },
            component: PeerReviewComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
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
    ],
  },
  {
    path: 'peer-review-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Wait peer review' },
        component: ReviewContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BdrRoutingModule {}
