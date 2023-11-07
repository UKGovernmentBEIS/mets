import { inject, NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { PeerReviewComponent } from '../../shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '../../shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { CivilPenaltyNoticeComponent } from './civil-penalty-notice/civil-penalty-notice.component';
import { NotifyOperatorComponent as CivilPenaltyNotifyOperatorComponent } from './civil-penalty-notice/notify-operator/notify-operator.component';
import { NotifyOperatorGuard as CivilPenaltyNotifyOperatorGuard } from './civil-penalty-notice/notify-operator/notify-operator.guard';
import { PeerReviewComponent as CivilPenaltyPeerReviewComponent } from './civil-penalty-notice/peer-review/peer-review.component';
import { PeerReviewGuard as CivilPenaltyPeerReviewGuard } from './civil-penalty-notice/peer-review/peer-review.guard';
import { PeerReviewWaitComponent as CivilPenaltyPeerReviewWaitComponent } from './civil-penalty-notice/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as CivilPenaltySummaryComponent } from './civil-penalty-notice/summary/summary.component';
import { SummaryGuard as CivilPenaltySummaryGuard } from './civil-penalty-notice/summary/summary.guard';
import { UploadCivilPenaltyComponent } from './civil-penalty-notice/upload-civil-penalty/upload-civil-penalty.component';
import { UploadCivilPenaltyGuard } from './civil-penalty-notice/upload-civil-penalty/upload-civil-penalty.guard';
import { CloseComponent } from './close/close.component';
import { ConfirmationComponent as CloseConfirmationComponent } from './close/confirmation/confirmation.component';
import { ConclusionComponent } from './conclusion/conclusion.component';
import { ConfirmationComponent as ConfirmationConfirmationComponent } from './conclusion/confirmation/confirmation.component';
import { ProvideConclusionComponent } from './conclusion/provide-conclusion/provide-conclusion.component';
import { ProvideConclusionGuard } from './conclusion/provide-conclusion/provide-conclusion.guard';
import { SummaryComponent as ConclusionSummaryComponent } from './conclusion/summary/summary.component';
import { SummaryGuard as ConclusionSummaryGuard } from './conclusion/summary/summary.guard';
import { DailyPenaltyNoticeComponent } from './daily-penalty-notice/daily-penalty-notice.component';
import { NotifyOperatorComponent as DailyPenaltyNoticeNotifyOperatorComponent } from './daily-penalty-notice/notify-operator/notify-operator.component';
import { NotifyOperatorGuard as DailyPenaltyNoticeNotifyOperatorGuard } from './daily-penalty-notice/notify-operator/notify-operator.guard';
import { PeerReviewComponent as DailyPenaltyNoticePeerReviewComponent } from './daily-penalty-notice/peer-review/peer-review.component';
import { PeerReviewGuard as DailyPenaltyNoticePeerReviewGuard } from './daily-penalty-notice/peer-review/peer-review.guard';
import { PeerReviewWaitComponent as DailyPenaltyNoticePeerReviewWaitComponent } from './daily-penalty-notice/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as DailyPenaltyNoticeSummaryComponent } from './daily-penalty-notice/summary/summary.component';
import { SummaryGuard as DailyPenaltyNoticeSummaryGuard } from './daily-penalty-notice/summary/summary.guard';
import { UploadInitialNoticeComponent } from './daily-penalty-notice/upload-initial-notice/upload-initial-notice.component';
import { UploadInitialNoticeGuard } from './daily-penalty-notice/upload-initial-notice/upload-initial-notice.guard';
import { NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE } from './non-compliance.module';
import { NoticeOfIntentComponent as NoticeOfIntentTaskComponent } from './notice-of-intent/notice-of-intent.component';
import { NotifyOperatorComponent as NoticeOfIntentNotifyOperatorComponent } from './notice-of-intent/notify-operator/notify-operator.component';
import { NotifyOperatorGuard as NoticeOfIntentNotifyOperatorGuard } from './notice-of-intent/notify-operator/notify-operator.guard';
import { PeerReviewComponent as NoticeOfIntentPeerReviewComponent } from './notice-of-intent/peer-review/peer-review.component';
import { PeerReviewGuard as NoticeOfIntentPeerReviewGuard } from './notice-of-intent/peer-review/peer-review.guard';
import { PeerReviewWaitComponent as NoticeOfIntentPeerReviewWaitComponent } from './notice-of-intent/peer-review-wait/peer-review-wait.component';
import { SummaryComponent as NoticeOfIntentSummaryComponent } from './notice-of-intent/summary/summary.component';
import { SummaryGuard as NoticeOfIntentSummaryGuard } from './notice-of-intent/summary/summary.guard';
import { UploadNoticeOfIntentComponent } from './notice-of-intent/upload-notice-of-intent/upload-notice-of-intent.component';
import { UploadNoticeOfIntentGuard } from './notice-of-intent/upload-notice-of-intent/upload-notice-of-intent.guard';
import { ChooseWorkflowComponent } from './submit/choose-workflow/choose-workflow.component';
import { ChooseWorkflowGuard } from './submit/choose-workflow/choose-workflow.guard';
import { ChooseWorkflowAddComponent } from './submit/choose-workflow/choose-workflow-add/choose-workflow-add.component';
import { ChooseWorkflowAddGuard } from './submit/choose-workflow/choose-workflow-add/choose-workflow-add.guard';
import { DeleteComponent } from './submit/choose-workflow/choose-workflow-add/delete/delete.component';
import { CivilPenaltyComponent } from './submit/civil-penalty/civil-penalty.component';
import { CivilPenaltyGuard } from './submit/civil-penalty/civil-penalty.guard';
import { DailyPenaltyComponent } from './submit/civil-penalty/daily-penalty/daily-penalty.component';
import { DailyPenaltyGuard } from './submit/civil-penalty/daily-penalty/daily-penalty.guard';
import { NoticeOfIntentComponent } from './submit/civil-penalty/notice-of-intent/notice-of-intent.component';
import { NoticeOfIntentGuard } from './submit/civil-penalty/notice-of-intent/notice-of-intent.guard';
import { ConfirmationComponent } from './submit/confirmation/confirmation.component';
import { DetailsOfBreachComponent } from './submit/details-of-breach/details-of-breach.component';
import { DetailsOfBreachGuard } from './submit/details-of-breach/details-of-breach.guard';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent as SubmitSummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard as SubmitSummaryGuard } from './submit/summary/summary.guard';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Provide details of breach: non-compliance' },
        canActivate: [
          (_, state) => {
            const router = inject(Router);
            const shouldRouteSubmit = inject(NON_COMPLIANCE_SUBMIT_PAGE_ROUTABLE) === true;
            return shouldRouteSubmit
              ? true
              : router
                  .navigateByUrl(state.url.includes('/aviation') ? '/aviation/dashboard' : '/dashboard')
                  .then(() => false);
          },
        ],
        component: SubmitContainerComponent,
      },
      {
        path: 'details-of-breach',
        data: { pageTitle: 'Non compliance submit details of breach' },
        component: DetailsOfBreachComponent,
        canActivate: [DetailsOfBreachGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'choose-workflow',
        children: [
          {
            path: '',
            data: { pageTitle: 'Non compliance submit workflow', backlink: '../details-of-breach' },
            component: ChooseWorkflowComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Non compliance submit workflow - add an item', backlink: '../' },
            component: ChooseWorkflowAddComponent,
            canActivate: [ChooseWorkflowAddGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index',
            children: [
              {
                path: '',
                data: { pageTitle: 'Non compliance submit workflow - add an item', backlink: '../' },
                component: ChooseWorkflowAddComponent,
                canActivate: [ChooseWorkflowAddGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'delete',
                data: { pageTitle: 'Non compliance submit workflow - Delete item', backlink: '../..' },
                component: DeleteComponent,
                canActivate: [ChooseWorkflowAddGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
        ],
        canActivate: [ChooseWorkflowGuard],
      },
      {
        path: 'civil-penalty',
        data: { pageTitle: 'Non compliance submit civil penalty', backlink: '../choose-workflow' },
        component: CivilPenaltyComponent,
        canActivate: [CivilPenaltyGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notice-of-intent',
        data: { pageTitle: 'Non compliance submit notice of intent', backlink: '../civil-penalty' },
        component: NoticeOfIntentComponent,
        canActivate: [NoticeOfIntentGuard],
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'daily-penalty',
        data: { pageTitle: 'Non compliance submit daily penalty', backlink: '../notice-of-intent' },
        component: DailyPenaltyComponent,
        canActivate: [DailyPenaltyGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Non compliance submit summary', breadcrumb: 'Summary' },
        component: SubmitSummaryComponent,
        canActivate: [SubmitSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: ConfirmationComponent,
        data: { pageTitle: 'Non-compliance details completed' },
      },
    ],
  },
  {
    path: 'close',
    data: { breadcrumb: 'Close task' },
    children: [
      {
        path: '',
        component: CloseComponent,
        data: { pageTitle: 'Task close' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: CloseConfirmationComponent,
        data: { pageTitle: 'Task close confirmation' },
      },
    ],
  },
  {
    path: 'daily-penalty-notice',
    children: [
      {
        path: '',
        component: DailyPenaltyNoticeComponent,
        data: { pageTitle: 'Upload initial penalty notice: non-compliance' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-initial-notice',
        component: UploadInitialNoticeComponent,
        data: { pageTitle: 'Upload daily penalty notice' },
        canActivate: [UploadInitialNoticeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: DailyPenaltyNoticeSummaryComponent,
        data: { pageTitle: 'Daily penalty notice summary' },
        canActivate: [DailyPenaltyNoticeSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator', breadcrumb: 'Notify operator' },
        component: DailyPenaltyNoticeNotifyOperatorComponent,
        canActivate: [DailyPenaltyNoticeNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canActivate: [DailyPenaltyNoticePeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review-wait',
        children: [
          {
            path: '',
            data: { pageTitle: 'Wait peer review' },
            component: DailyPenaltyNoticePeerReviewWaitComponent,
          },
        ],
      },
      {
        path: 'dpn-peer-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review' },
            component: DailyPenaltyNoticePeerReviewComponent,
          },
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
                data: { pageTitle: 'Peer review decision answers' },
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
    ],
  },
  {
    path: 'notice-of-intent',
    children: [
      {
        path: '',
        component: NoticeOfIntentTaskComponent,
        data: { pageTitle: 'Upload notice of intent: non-compliance' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-notice-of-intent',
        component: UploadNoticeOfIntentComponent,
        data: { pageTitle: 'Upload notice of intent' },
        canActivate: [UploadNoticeOfIntentGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: NoticeOfIntentSummaryComponent,
        data: { pageTitle: 'Notice of intent summary' },
        canActivate: [NoticeOfIntentSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator', breadcrumb: 'Notify operator' },
        component: NoticeOfIntentNotifyOperatorComponent,
        canActivate: [NoticeOfIntentNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canActivate: [NoticeOfIntentPeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review-wait',
        children: [
          {
            path: '',
            data: { pageTitle: 'Wait peer review' },
            component: NoticeOfIntentPeerReviewWaitComponent,
          },
        ],
      },
      {
        path: 'noi-peer-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review' },
            component: NoticeOfIntentPeerReviewComponent,
          },
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
                data: { pageTitle: 'Peer review decision answers' },
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
    ],
  },
  {
    path: 'civil-penalty-notice',
    children: [
      {
        path: '',
        component: CivilPenaltyNoticeComponent,
        data: { pageTitle: 'Upload penalty notice: non-compliance' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-penalty-notice',
        component: UploadCivilPenaltyComponent,
        data: { pageTitle: 'Upload penalty notice' },
        canActivate: [UploadCivilPenaltyGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: CivilPenaltySummaryComponent,
        data: { pageTitle: 'Civil penalty notice summary' },
        canActivate: [CivilPenaltySummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator', breadcrumb: 'Notify operator' },
        component: CivilPenaltyNotifyOperatorComponent,
        canActivate: [CivilPenaltyNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canActivate: [CivilPenaltyPeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review-wait',
        children: [
          {
            path: '',
            data: { pageTitle: 'Wait peer review' },
            component: CivilPenaltyPeerReviewWaitComponent,
          },
        ],
      },
      {
        path: 'cpn-peer-review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review' },
            component: CivilPenaltyPeerReviewComponent,
          },
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
                data: { pageTitle: 'Peer review decision answers' },
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
    ],
  },
  {
    path: 'conclusion',
    children: [
      {
        path: '',
        component: ConclusionComponent,
        data: { pageTitle: 'Provide conclusion of non-compliance' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'provide-conclusion',
        component: ProvideConclusionComponent,
        data: { pageTitle: 'Provide conclusion of non-compliance' },
        canActivate: [ProvideConclusionGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ConclusionSummaryComponent,
        data: { pageTitle: 'Conclusion summary' },
        canActivate: [ConclusionSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: ConfirmationConfirmationComponent,
        data: { pageTitle: 'Conclusion of non-compliance complete' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NonComplianceRoutingModule {}
