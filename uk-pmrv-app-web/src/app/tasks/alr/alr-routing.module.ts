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
  AlrApprovedAllocationsComponent,
  AlrAuthorityDateSubmittedComponent,
  AlrAuthorityNotifyOperatorComponent,
  AlrAuthoritySummaryComponent,
  AlrAuthorityTaskListComponent,
  AlrAuthorityUploadLatestActivityComponent,
  AlrAuthorityuploadLatestAlrSummaryComponent,
  AlrPreliminaryAllocationComponent as AlrAuthorityResponsePreliminaryAllocationComponent,
  AlrPreliminaryAllocationDeleteComponent as AlrAuthorityResponsePreliminaryAllocationDeleteComponent,
  ALRPreliminaryAllocationsComponent as ALRAuthorityResponsePreliminaryAllocationsComponent,
  AlrResponseComponent,
  AlrResponseSummaryComponent,
} from './authority';
import {
  AlrPreliminaryAllocationGuard as AlrAuthorityResponsePreliminaryAllocationGuard,
  AlrReponseSummaryGuard,
  dateSubmittedSummaryGuard,
} from './authority/guards';
import {
  ActivityReviewComponent,
  ALCInformationSummaryComponent,
  AlrActivityLevelComponent,
  AlrActivityLevelDeleteComponent,
  AlrActivityLevelsComponent,
  AlrAllocationComponent,
  AlrAllocationDeleteComponent,
  AlrAllocationsComponent,
  AlrCommentsComponent,
  AlrCompleteTaskComponent,
  AlrDeterminationComponent,
  AlrDeterminationSummaryComponent,
  AlrEstimatesComponent,
  AlrLatestActivityComponent,
  AlrNotifyOperatorComponent,
  AlrOpinionStatementReviewComponent,
  AlrOverallDecisionReviewComponent,
  AlrPreliminaryAllocationComponent,
  AlrProceedAuthorityReasonComponent,
  AlrReasonComponent,
  AlrReturnForAmendsComponent,
  AlrReviewTaskListComponent,
  AlrUploadLatestActivityComponent,
  AlrWithholdingOfAllowancesComponent,
} from './review';
import {
  alcInformationSummaryGuard,
  AlrDeterminationCloseGuard,
  AlrDeterminationGuard,
  AlrDeterminationProceedAuthorityGuard,
  alrDeterminationSummaryGuard,
} from './review/guards';
import { AlrSendReportComponent, AlrSendReportQuestionComponent } from './shared';
import {
  ActivitySummaryGuard,
  AlrChangesRequestedComponent,
  AlrFileSummaryComponent,
  AlrSendReportGuard,
  AlrTaskListComponent,
  ALRUploadReportComponent,
} from './submit';
import {
  alrSendReportBacklinkResolver,
  alrSendReportTitleResolver,
  alrUploadActivityBacklinkResolver,
  determinationBacklinkResolver,
} from './utils';
import {
  ActivityVerifierReviewComponent,
  AlrOpinionStatementSummaryComponent,
  AlrReturnToOperatorComponent,
  AlrReturnToOperatorSummaryComponent,
  AlrUploadOpinionStatementComponent,
  OverallDecisionAssessmentComponent,
  OverallDecisionSummaryComponent,
  returnToOperatorFormProvider,
  VerificationSubmitTaskListComponent,
} from './verification-submit';
import {
  opinionStatementSummaryGuard,
  overallDecisionSummaryGuard,
  returnToOperatorGuard,
} from './verification-submit/guards';

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
            component: AlrFileSummaryComponent,
            providers: [ActivitySummaryGuard],
            canActivate: [ActivitySummaryGuard],
          },
        ],
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            resolve: {
              alrTitle: alrSendReportTitleResolver,
              pageTitle: alrSendReportTitleResolver,
              backlink: alrSendReportBacklinkResolver,
            },
            providers: [AlrSendReportGuard],
            canActivate: [AlrSendReportGuard],
            component: AlrSendReportComponent,
          },
          {
            path: 'question',
            data: { pageTitle: 'Upload the activity level report file' },
            component: AlrSendReportQuestionComponent,
          },
        ],
      },
      {
        path: 'recall-from-verifier',
        data: { pageTitle: 'Recall the report', breadcrumb: true },
        component: RecallSharedComponent,
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        component: AlrChangesRequestedComponent,
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
            data: { pageTitle: 'Changes required by the operator' },
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
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: ' Review activity level report' },
        component: AlrReviewTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'activity',
        data: { pageTitle: 'Activity level report and details', groupKey: 'ALR', breadcrumb: true },
        component: ActivityReviewComponent,
      },
      {
        path: 'return-for-amends',
        data: { pageTitle: 'Return for amends', breadcrumb: true },
        component: AlrReturnForAmendsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'ALR verification opinion statement', groupKey: 'OPINION_STATEMENT', breadcrumb: true },
        component: AlrOpinionStatementReviewComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'ALR overall decision', groupKey: 'OVERALL_DECISION', breadcrumb: true },
        component: AlrOverallDecisionReviewComponent,
      },
      {
        path: 'alc-information',
        children: [
          {
            path: '',
            redirectTo: 'activity-levels',
            pathMatch: 'full',
          },
          {
            path: 'activity-levels',
            children: [
              {
                path: '',
                data: { pageTitle: 'Provide updated activity level', groupKey: 'ALC', breadcrumb: true },
                component: AlrActivityLevelsComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'New activity level change - create',
                  breadcrumb: true,
                },
                component: AlrActivityLevelComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index',
                children: [
                  {
                    path: '',
                    data: {
                      pageTitle: 'New activity level change - update',
                      breadcrumb: true,
                    },
                    component: AlrActivityLevelComponent,
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'New activity level change - Delete',
                      breadcrumb: true,
                    },
                    component: AlrActivityLevelDeleteComponent,
                    canDeactivate: [PendingRequestGuard],
                  },
                ],
              },
            ],
          },
          {
            path: 'estimates',
            data: {
              pageTitle: 'Information about this activity level change - Conservative Estimates',
              breadcrumb: true,
              backlink: '../activity-levels',
            },
            component: AlrEstimatesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'preliminary-allocations',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Information about this activity level change - Allocations',
                  breadcrumb: true,
                  backlink: '../estimates',
                },
                component: AlrAllocationsComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'Information about this activity level change  - Allocations - create',
                  breadcrumb: true,
                },
                component: AlrAllocationComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index',
                children: [
                  {
                    path: '',
                    data: {
                      pageTitle: 'Information about this activity level change',
                      breadcrumb: true,
                    },
                    component: AlrAllocationComponent,
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'Information about this activity level change - Delete',
                      breadcrumb: true,
                    },
                    component: AlrAllocationDeleteComponent,
                    canDeactivate: [PendingRequestGuard],
                  },
                ],
              },
            ],
          },
          {
            path: 'comments',
            data: {
              pageTitle: 'Information about this activity level change - Comments for UK ETS Authority',
              breadcrumb: 'Information about this activity level change',
              backlink: '../preliminary-allocations',
            },
            component: AlrCommentsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Information about this activity level change - Summary',
              breadcrumb: 'Information about this activity level change',
            },
            component: ALCInformationSummaryComponent,
            canActivate: [alcInformationSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'determination',
        children: [
          {
            path: '',
            data: { pageTitle: 'Determination' },
            component: AlrDeterminationComponent,
            canActivate: [AlrDeterminationGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'close',
            children: [
              {
                path: '',
                redirectTo: 'reason',
                pathMatch: 'full',
              },
              {
                path: 'reason',
                data: { pageTitle: 'Close - reason', backlink: ({ backlinkUrl }) => backlinkUrl || '../..' },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                canActivate: [AlrDeterminationCloseGuard],
                component: AlrReasonComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'latest-activity',
                data: {
                  pageTitle: 'Upload the latest activity level report file',
                  backlink: ({ backlinkUrl }) => backlinkUrl || '../reason',
                },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                component: AlrLatestActivityComponent,
              },
              {
                path: 'upload-latest-activity',
                data: {
                  pageTitle: 'Upload the latest activity level report file',
                  backlink: ({ backlinkUrl }) => backlinkUrl || '../latest-activity',
                },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                component: AlrUploadLatestActivityComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'summary',
                data: {
                  pageTitle: 'Close - Determination summary',
                  breadcrumb: true,
                },
                component: AlrDeterminationSummaryComponent,
                canActivate: [alrDeterminationSummaryGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'proceed-authority',
            children: [
              {
                path: '',
                redirectTo: 'reason',
                pathMatch: 'full',
              },
              {
                path: 'reason',
                data: {
                  pageTitle: 'Proceed authority - reason',
                  backlink: ({ backlinkUrl }) => backlinkUrl || '../..',
                },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                component: AlrProceedAuthorityReasonComponent,
                canActivate: [AlrDeterminationProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'withholding-of-allowances',
                data: {
                  pageTitle: 'Has a withholding of allowances notice been issued?',
                  backlink: ({ backlinkUrl }) => backlinkUrl || '../reason',
                },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                component: AlrWithholdingOfAllowancesComponent,
                canActivate: [AlrDeterminationProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'preliminary-allocation',
                data: {
                  pageTitle: 'Will you send a preliminary allocation letter?',
                  backlink: ({ backlinkUrl }) => backlinkUrl || '../withholding-of-allowances',
                },
                resolve: { backlinkUrl: determinationBacklinkResolver },
                component: AlrPreliminaryAllocationComponent,
                canActivate: [AlrDeterminationProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'summary',
                data: {
                  pageTitle: 'Proceed authority - Determination summary',
                  breadcrumb: true,
                },
                component: AlrDeterminationSummaryComponent,
                canActivate: [alrDeterminationSummaryGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
        ],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator of decision', breadcrumb: true },
        component: AlrNotifyOperatorComponent,
      },
      {
        path: 'complete-task',
        data: { pageTitle: 'Complete the activity level report task', breadcrumb: true },
        component: AlrCompleteTaskComponent,
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Send for peer review', breadcrumb: true },
        component: PeerReviewComponent,
        canDeactivate: [PendingRequestGuard],
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
    path: 'authority',
    children: [
      {
        path: '',
        data: { pageTitle: 'Authority response activity level report' },
        component: AlrAuthorityTaskListComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'application-submitted',
        children: [
          {
            path: '',
            redirectTo: 'summary',
            pathMatch: 'full',
          },
          {
            path: 'date',
            data: { pageTitle: 'Provide the date application was submitted to the authority' },
            component: AlrAuthorityDateSubmittedComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Date submitted to authority - Summary', breadcrumb: true },
            component: AlrAuthoritySummaryComponent,
            canActivate: [dateSubmittedSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'response',
        data: { sectionKey: 'authorityResponse' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Provide UK ETS Authority response',
              breadcrumb: 'Provide UK ETS Authority response',
            },
            component: AlrResponseComponent,
          },
          {
            path: 'preliminary-allocations',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Provide authority approved allocation for each sub-installation',
                  breadcrumb: 'Provide authority approved allocation for each sub-installation',
                  backlink: '..',
                },
                component: ALRAuthorityResponsePreliminaryAllocationsComponent,
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'Allocation - create',
                  breadcrumb: 'Allocation',
                  backlink: '..',
                },
                component: AlrAuthorityResponsePreliminaryAllocationComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index',
                children: [
                  {
                    path: '',
                    data: {
                      pageTitle: 'Allocation',
                      breadcrumb: 'Allocation',
                      backlink: '..',
                    },
                    component: AlrAuthorityResponsePreliminaryAllocationComponent,
                    canActivate: [AlrAuthorityResponsePreliminaryAllocationGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'Allocation - Delete',
                      breadcrumb: 'Provide authority approved allocation for each sub-installation',
                    },
                    component: AlrAuthorityResponsePreliminaryAllocationDeleteComponent,
                    canActivate: [AlrAuthorityResponsePreliminaryAllocationGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                ],
              },
            ],
          },
          {
            path: 'approved-allocations',
            data: {
              pageTitle: 'Authority approved allocations',
              breadcrumb: 'Authority approved allocations',
              backlink: '../preliminary-allocations',
            },
            component: AlrApprovedAllocationsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Provide UK ETS Authority response - Summary',
              breadcrumb: 'Provide UK ETS Authority response',
            },
            component: AlrResponseSummaryComponent,
            canActivate: [AlrReponseSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'upload-activity-level-report',
        children: [
          {
            path: '',
            redirectTo: 'summary',
            pathMatch: 'full',
          },
          {
            path: 'upload-latest-activity',
            data: {
              pageTitle: 'Upload the latest activity level report file',
              backlink: ({ backlinkUrl }) => backlinkUrl,
            },
            resolve: { backlinkUrl: alrUploadActivityBacklinkResolver },
            component: AlrAuthorityUploadLatestActivityComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Latest activity level report - Summary',
              breadcrumb: true,
            },
            component: AlrAuthorityuploadLatestAlrSummaryComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator of decision', breadcrumb: true },
        component: AlrAuthorityNotifyOperatorComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AlrRoutingModule {}
