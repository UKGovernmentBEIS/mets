import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PeerReviewComponent as PeerReviewSubmitComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { AuthorityResponseContainerComponent } from '@tasks/doal/authority-response/authority-response-container.component';
import { DateSubmittedComponent } from '@tasks/doal/authority-response/date-submitted/date-submitted.component';
import { SummaryComponent as DateSubmittedSummaryComponent } from '@tasks/doal/authority-response/date-submitted/summary/summary.component';
import { NotifyOperatorComponent as AuthorityResponseNotifyOperatorComponent } from '@tasks/doal/authority-response/notify-operator/notify-operator.component';
import { NotifyOperatorGuard as AuthorityResponseNotifyOperatorGuard } from '@tasks/doal/authority-response/notify-operator/notify-operator.guard';
import { ApprovedAllocationsComponent } from '@tasks/doal/authority-response/response/approved-allocations/approved-allocations.component';
import { PreliminaryAllocationGuard as AuthorityResponsePreliminaryAllocationGuard } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation.guard';
import { DeleteComponent as AuthorityResponseDeleteComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/delete/delete.component';
import { PreliminaryAllocationComponent as AuthorityResponsePreliminaryAllocationComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocation/preliminary-allocation.component';
import { PreliminaryAllocationsComponent as AuthorityResponsePreliminaryAllocationsComponent } from '@tasks/doal/authority-response/response/preliminary-allocations/preliminary-allocations.component';
import { ResponseComponent } from '@tasks/doal/authority-response/response/response.component';
import { SummaryComponent as ResponseSummaryComponent } from '@tasks/doal/authority-response/response/summary/summary.component';
import { SummaryGuard as AuthoritySummaryGuard } from '@tasks/doal/authority-response/summary.guard';
import { PeerReviewComponent } from '@tasks/doal/peer-review/peer-review.component';
import { PeerReviewWaitComponent } from '@tasks/doal/peer-review-wait/peer-review-wait.component';
import { AdditionalDocumentsSummaryComponent } from '@tasks/doal/shared/components/additional-documents-summary/additional-documents-summary.component';
import { AlcInformationSummaryComponent } from '@tasks/doal/shared/components/alc-information-summary/alc-information-summary.component';
import { DeterminationSummaryComponent } from '@tasks/doal/shared/components/determination-summary/determination-summary.component';
import { OperatorReportSummaryComponent } from '@tasks/doal/shared/components/operator-report-summary/operator-report-summary.component';
import { VerificationReportSummaryComponent } from '@tasks/doal/shared/components/verification-report-summary/verification-report-summary.component';
import { SendPeerReviewGuard } from '@tasks/doal/submit/send-peer-review.guard';

import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { SummaryComponent as AdditionalDocumentsSubmitSummaryComponent } from './submit/additional-documents/summary/summary.component';
import { ActivityLevelComponent } from './submit/alc-information/activity-levels/activity-level/activity-level.component';
import { ActivityLevelGuard } from './submit/alc-information/activity-levels/activity-level/activity-level.guard';
import { DeleteComponent as ActivityLevelDeleteComponent } from './submit/alc-information/activity-levels/activity-level/delete/delete.component';
import { ActivityLevelsComponent } from './submit/alc-information/activity-levels/activity-levels.component';
import { AlcInformationGuard } from './submit/alc-information/alc-information.guard';
import { CommentsComponent } from './submit/alc-information/comments/comments.component';
import { EstimatesComponent } from './submit/alc-information/estimates/estimates.component';
import { DeleteComponent as PreliminaryAllocationDeleteComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocation/delete/delete.component';
import { PreliminaryAllocationComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocation/preliminary-allocation.component';
import { PreliminaryAllocationGuard } from './submit/alc-information/preliminary-allocations/preliminary-allocation/preliminary-allocation.guard';
import { PreliminaryAllocationsComponent } from './submit/alc-information/preliminary-allocations/preliminary-allocations.component';
import { SummaryComponent as ALCInformationSubmitSummaryComponent } from './submit/alc-information/summary/summary.component';
import { CompleteComponent } from './submit/complete/complete.component';
import { CompleteGuard } from './submit/complete/complete.guard';
import { CloseGuard } from './submit/determination/close/close.guard';
import { ReasonComponent as CloseReasonComponent } from './submit/determination/close/reason/reason.component';
import { DeterminationComponent } from './submit/determination/determination.component';
import { DeterminationGuard } from './submit/determination/determination.guard';
import { OfficialNoticeComponent as ProceedAuthorityOfficialNoticeComponent } from './submit/determination/proceed-authority/official-notice/official-notice.component';
import { ProceedAuthorityGuard } from './submit/determination/proceed-authority/proceed-authority.guard';
import { ReasonComponent as ProceedAuthorityReasonComponent } from './submit/determination/proceed-authority/reason/reason.component';
import { WithholdingComponent as ProceedAuthorityWithholdingComponent } from './submit/determination/proceed-authority/withholding/withholding.component';
import { SummaryComponent as DeterminationSubmitSummaryComponent } from './submit/determination/summary/summary.component';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './submit/notify-operator/notify-operator.guard';
import { OperatorReportComponent } from './submit/operator-report/operator-report.component';
import { SummaryComponent as OperatorReportSubmitSummaryComponent } from './submit/operator-report/summary/summary.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryGuard } from './submit/summary.guard';
import { SummaryComponent as VerificationReportSubmitSummaryComponent } from './submit/verification-report/summary/summary.component';
import { VerificationReportComponent } from './submit/verification-report/verification-report.component';

const sharedPeerReviewRoutes: Routes = [
  {
    path: 'operator-report/summary',
    data: {
      pageTitle: 'Upload operator activity level report - Summary',
      breadcrumb: 'Upload operator activity level report',
    },
    component: OperatorReportSummaryComponent,
  },
  {
    path: 'verification-report/summary',
    data: {
      pageTitle: 'Upload verification report of the activity level report - Summary',
      breadcrumb: 'Upload verification report of the activity level report',
    },
    component: VerificationReportSummaryComponent,
  },
  {
    path: 'additional-documents/summary',
    data: {
      pageTitle: 'Upload additional documents - Summary',
      breadcrumb: 'Upload additional documents',
    },
    component: AdditionalDocumentsSummaryComponent,
  },
  {
    path: 'alc-information/summary',
    data: {
      pageTitle: 'Information about this activity level determination - Summary',
      breadcrumb: 'Information about this activity level determination',
    },
    component: AlcInformationSummaryComponent,
  },
  {
    path: 'determination/close/summary',
    data: {
      pageTitle: 'Provide determination of activity level - Summary',
      breadcrumb: 'Provide determination of activity level',
    },
    component: DeterminationSummaryComponent,
  },
  {
    path: 'determination/proceed-authority/summary',
    data: {
      pageTitle: 'Provide determination of activity level - Summary',
      breadcrumb: 'Provide determination of activity level',
    },
    component: DeterminationSummaryComponent,
  },
];

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Activity level change submit' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'operator-report',
        data: { sectionKey: 'operatorActivityLevelReport' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Upload operator activity level report',
              breadcrumb: 'Upload operator activity level report',
            },
            component: OperatorReportComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Upload operator activity level report - Summary',
              breadcrumb: 'Upload operator activity level report',
            },
            component: OperatorReportSubmitSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'verification-report',
        data: { sectionKey: 'verificationReportOfTheActivityLevelReport' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Upload verification report of the activity level report',
              breadcrumb: 'Upload verification report of the activity level report',
            },
            component: VerificationReportComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Upload verification report of the activity level report - Summary',
              breadcrumb: 'Upload verification report of the activity level report',
            },
            component: VerificationReportSubmitSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'additional-documents',
        data: { sectionKey: 'additionalDocuments' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Upload additional documents',
              breadcrumb: 'Upload additional documents',
            },
            component: AdditionalDocumentsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Upload additional documents - Summary',
              breadcrumb: 'Upload additional documents',
            },
            component: AdditionalDocumentsSubmitSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'alc-information',
        data: { sectionKey: 'activityLevelChangeInformation' },
        children: [
          {
            path: '',
            redirectTo: 'activity-levels',
            pathMatch: 'full',
          },
          {
            path: 'activity-levels',
            canActivate: [AlcInformationGuard],
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Information about this activity level determination',
                  breadcrumb: 'Information about this activity level determination',
                },
                component: ActivityLevelsComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'Information about this activity level determination - create',
                  breadcrumb: 'Information about this activity level determination',
                },
                component: ActivityLevelComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index',
                children: [
                  {
                    path: '',
                    data: {
                      pageTitle: 'Information about this activity level determination',
                      breadcrumb: 'Information about this activity level determination',
                    },
                    component: ActivityLevelComponent,
                    canActivate: [ActivityLevelGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'Information about this activity level determination - Delete',
                      breadcrumb: 'Information about this activity level determination',
                    },
                    component: ActivityLevelDeleteComponent,
                    canActivate: [ActivityLevelGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                ],
              },
            ],
          },
          {
            path: 'estimates',
            data: {
              pageTitle: 'Information about this activity level determination - Conservative Estimates',
              breadcrumb: 'Information about this activity level determination',
              backlink: '../activity-levels',
            },
            component: EstimatesComponent,
            canActivate: [AlcInformationGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'preliminary-allocations',
            canActivate: [AlcInformationGuard],
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Information about this activity level determination - Preliminary allocations',
                  breadcrumb: 'Information about this activity level determination',
                  backlink: '../estimates',
                },
                component: PreliminaryAllocationsComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'Information about this activity level determination  - Preliminary allocations - create',
                  breadcrumb: 'Information about this activity level determination',
                },
                component: PreliminaryAllocationComponent,
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index',
                children: [
                  {
                    path: '',
                    data: {
                      pageTitle: 'Information about this activity level determination',
                      breadcrumb: 'Information about this activity level determination',
                    },
                    component: PreliminaryAllocationComponent,
                    canActivate: [PreliminaryAllocationGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'Information about this activity level determination - Delete',
                      breadcrumb: 'Information about this activity level determination',
                    },
                    component: PreliminaryAllocationDeleteComponent,
                    canActivate: [PreliminaryAllocationGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                ],
              },
            ],
          },
          {
            path: 'comments',
            data: {
              pageTitle: 'Information about this activity level determination - Comments for UK ETS Authority',
              breadcrumb: 'Information about this activity level determination',
              backlink: '../preliminary-allocations',
            },
            component: CommentsComponent,
            canActivate: [AlcInformationGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Information about this activity level determination - Summary',
              breadcrumb: 'Information about this activity level determination',
            },
            component: ALCInformationSubmitSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'determination',
        data: { sectionKey: 'determination' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Determination',
              breadcrumb: 'Determination',
            },
            component: DeterminationComponent,
            canActivate: [DeterminationGuard],
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
                data: {
                  pageTitle: 'Close - reason',
                  breadcrumb: 'Determination',
                  backlink: '../..',
                },
                component: CloseReasonComponent,
                canActivate: [CloseGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'summary',
                data: {
                  pageTitle: 'Close - summary',
                  breadcrumb: 'Determination',
                },
                component: DeterminationSubmitSummaryComponent,
                canActivate: [SummaryGuard],
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
                  breadcrumb: 'Determination',
                  backlink: '../..',
                },
                component: ProceedAuthorityReasonComponent,
                canActivate: [ProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'withholding',
                data: {
                  pageTitle: 'Proceed authority - withholding of allowances',
                  breadcrumb: 'Determination',
                  backlink: '../reason',
                },
                component: ProceedAuthorityWithholdingComponent,
                canActivate: [ProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'official-notice',
                data: {
                  pageTitle: 'Proceed authority - official notice',
                  breadcrumb: 'Determination',
                  backlink: '../withholding',
                },
                component: ProceedAuthorityOfficialNoticeComponent,
                canActivate: [ProceedAuthorityGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'summary',
                data: {
                  pageTitle: 'Proceed authority - summary',
                  breadcrumb: 'Determination',
                },
                component: DeterminationSubmitSummaryComponent,
                canActivate: [SummaryGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
        ],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: NotifyOperatorComponent,
        canActivate: [NotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'complete',
        data: { pageTitle: 'Complete' },
        component: CompleteComponent,
        canActivate: [CompleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewSubmitComponent,
        canActivate: [SendPeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'cancel',
        loadChildren: () => import('@cancel-task/cancel-task.module').then((m) => m.CancelTaskModule),
      },
    ],
  },
  {
    path: 'authority-response',
    children: [
      {
        path: '',
        data: { pageTitle: 'Provide UK ETS authority response for activity Level Change' },
        component: AuthorityResponseContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'date-submitted',
        data: { sectionKey: 'dateSubmittedToAuthority' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Provide the date application was submitted to the authority',
              breadcrumb: 'Provide the date application was submitted to the authority',
            },
            component: DateSubmittedComponent,
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Provide the date application was submitted to the authority - Summary',
              breadcrumb: 'Provide the date application was submitted to the authority',
            },
            component: DateSubmittedSummaryComponent,
            canActivate: [AuthoritySummaryGuard],
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
            component: ResponseComponent,
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
                component: AuthorityResponsePreliminaryAllocationsComponent,
              },
              {
                path: 'add',
                data: {
                  pageTitle: 'Allocation - create',
                  breadcrumb: 'Allocation',
                  backlink: '..',
                },
                component: AuthorityResponsePreliminaryAllocationComponent,
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
                    component: AuthorityResponsePreliminaryAllocationComponent,
                    canActivate: [AuthorityResponsePreliminaryAllocationGuard],
                    canDeactivate: [PendingRequestGuard],
                  },
                  {
                    path: 'delete',
                    data: {
                      pageTitle: 'Allocation - Delete',
                      breadcrumb: 'Provide authority approved allocation for each sub-installation',
                    },
                    component: AuthorityResponseDeleteComponent,
                    canActivate: [AuthorityResponsePreliminaryAllocationGuard],
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
            component: ApprovedAllocationsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Provide UK ETS Authority response - Summary',
              breadcrumb: 'Provide UK ETS Authority response',
            },
            component: ResponseSummaryComponent,
            canActivate: [AuthoritySummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: AuthorityResponseNotifyOperatorComponent,
        canActivate: [AuthorityResponseNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'peer-review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      ...sharedPeerReviewRoutes,
      {
        path: 'decision',
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
        component: PeerReviewWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      ...sharedPeerReviewRoutes,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DoalRoutingModule {}
