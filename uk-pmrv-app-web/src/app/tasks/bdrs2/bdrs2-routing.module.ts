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

import {
  BaselineReviewComponent,
  BdrS2CompleteReviewGuard,
  BdrS2OpinionStatementReviewComponent,
  BDRS2OutcomeCbamComponent,
  BDRS2OutcomeCovidAdjustmentsComponent,
  BDRS2OutcomeFaDecisionComponent,
  BDRS2OutcomeInstallationSectorComponent,
  bdrs2OutcomeReviewBacklinkResolver,
  BdrS2OverallDecisionReviewComponent,
  Bdrs2ReturnForAmendsComponent,
  OutcomeFileUploadComponent,
  OutcomeSummaryComponent,
  outcomeSummaryGuard,
  ReviewComponent,
} from './review';
import { BdrS2CompleteReviewComponent } from './review/complete-review/bdrs2-complete-review.component';
import { BdrS2CompleteConfirmationComponent } from './review/complete-review/confirmation/bdrs2-complete-confirmation.component';
import {
  OPINION_STATEMENT,
  OVERALL_DECISION,
} from './shared/components/decision/bdrs2-review-decision/bdrs2-verification-review-group-decision-form.util';
import {
  BDRS2BaselineStepGuard,
  BDRS2BaselineSummaryGuard,
  Bdrs2ChangesRequestedComponent,
  Bdrs2SendReportConfirmationComponent,
  Bdrs2SendReportGuard,
  Bdrs2SendReportRegulatorComponent,
  Bdrs2SendReportVerifierComponent,
  Bdrs2SendReportVerifierGuard,
  BdrS2SendVerifierOrRegulatorComponent,
  BDRS2SummaryComponent,
  BDRS2UploadMmpComponent,
  BDRS2UploadReportComponent,
  CBAMComponent,
  DetailsComponent,
  FreeAllocationComponent,
  SubmitComponent,
} from './submit';
import { bdrs2SendReportBacklinkResolver, bdrs2UploadFileBacklinkResolver } from './utils';
import {
  BaselineVerifierReviewComponent,
  BdrS2ReturnToOperatorComponent,
  BdrS2ReturnToOperatorSummaryComponent,
  OpinionStatementSummaryComponent,
  OpinionStatementSummaryGuard,
  OverallDecisionAssessmentComponent,
  OverallDecisionSummaryComponent,
  OverallDecisionSummaryGuard,
  returnToOperatorFormProvider,
  returnToOperatorGuard,
  SendBdrs2ReportComponent,
  UploadOpinionStatementComponent,
  VerificationSubmitComponent,
} from './verification-submit';

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
      {
        path: 'recall-bdrs2-from-verifier',
        data: { pageTitle: 'Recall the report', breadcrumb: true },
        component: RecallSharedComponent,
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: Bdrs2ChangesRequestedComponent,
      },
    ],
  },
  {
    path: 'verification-submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Stage 2 baseline data report' },
        component: VerificationSubmitComponent,
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
            component: BdrS2ReturnToOperatorComponent,
          },
          {
            path: 'summary',
            canActivate: [returnToOperatorGuard],
            data: { pageTitle: 'Check your answers', breadcrumb: true },
            component: BdrS2ReturnToOperatorSummaryComponent,
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
            canActivate: [OverallDecisionSummaryGuard],
            component: OverallDecisionSummaryComponent,
          },
        ],
      },
      {
        path: 'send-report',
        data: {
          pageTitle: 'Send report to operator',
        },
        component: SendBdrs2ReportComponent,
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review stage 2 baseline data report' },
        component: ReviewComponent,
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
            component: BdrS2CompleteReviewComponent,
            canActivate: [BdrS2CompleteReviewGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            component: BdrS2CompleteConfirmationComponent,
          },
        ],
      },
      {
        path: 'baseline',
        data: { pageTitle: 'Stage 2 baseline data report and details', groupKey: 'BDRS2', breadcrumb: true },
        component: BaselineReviewComponent,
      },
      {
        path: 'opinion-statement',
        data: {
          pageTitle: 'Stage 2 BDR verification opinion statement',
          groupKey: OPINION_STATEMENT,
          breadcrumb: true,
        },

        component: BdrS2OpinionStatementReviewComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'Stage 2 BDR overall decision', groupKey: OVERALL_DECISION, breadcrumb: true },

        component: BdrS2OverallDecisionReviewComponent,
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amendments', breadcrumb: true },
            component: Bdrs2ReturnForAmendsComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'outcome',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'What is your decision for the free allocation? - Outcome of regulator review',
            },
            component: BDRS2OutcomeFaDecisionComponent,
          },
          {
            path: 'covid-adjustments',
            data: {
              pageTitle: 'What is your decision on the COVID adjustments? - Outcome of regulator review',
              backlink: '../',
            },
            component: BDRS2OutcomeCovidAdjustmentsComponent,
          },
          {
            path: 'installation-sector',
            data: {
              pageTitle: 'What is your decision on the installation sector? - Outcome of regulator review',
              backlink: '../covid-adjustments',
            },
            component: BDRS2OutcomeInstallationSectorComponent,
          },
          {
            path: 'cbam',
            data: {
              pageTitle:
                'What is your decision on the sub-installation splits required because of the UK CBAM? - Outcome of regulator review',
              backlink: '../installation-sector',
            },
            component: BDRS2OutcomeCbamComponent,
          },
          {
            path: 'file-upload',
            data: {
              pageTitle: 'Upload the stage 2 baseline data report file - Outcome of regulator review',
              backlink: ({ backlinkUrl }) => backlinkUrl,
            },
            resolve: { backlinkUrl: bdrs2OutcomeReviewBacklinkResolver },
            component: OutcomeFileUploadComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers - Outcome of regulator review',
              backlink: false,
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
        component: ReviewComponent,
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
export class BdrS2RoutingModule {}
