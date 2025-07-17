import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';

import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '../../shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { PeerReviewComponent as DrePeerReview } from './peer-review/peer-review.component';
import { PeerReviewGuard } from './peer-review/peer-review.guard';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { ChargeOperatorComponent } from './submit/charge-operator/charge-operator.component';
import { ChargeOperatorGuard } from './submit/charge-operator/charge-operator.guard';
import { DeterminationReasonComponent } from './submit/determination-reason/determination-reason.component';
import { DeterminationReasonGuard } from './submit/determination-reason/determination-reason.guard';
import { FeeComponent } from './submit/fee/fee.component';
import { FeeGuard } from './submit/fee/fee.guard';
import { DeleteComponent } from './submit/information-sources/information-source/delete/delete.component';
import { InformationSourceComponent } from './submit/information-sources/information-source/information-source.component';
import { InformationSourceGuard } from './submit/information-sources/information-source/information-source.guard';
import { InformationSourcesComponent } from './submit/information-sources/information-sources.component';
import { InformationSourcesGuard } from './submit/information-sources/information-sources.guard';
import { MonitoringApproachesComponent } from './submit/monitoring-approaches/monitoring-approaches.component';
import { MonitoringApproachesGuard } from './submit/monitoring-approaches/monitoring-approaches.guard';
import { NotifyOperatorComponent } from './submit/notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './submit/notify-operator/notify-operator.guard';
import { OfficialNoticeReasonComponent } from './submit/official-notice-reason/official-notice-reason.component';
import { OfficialNoticeReasonGuard } from './submit/official-notice-reason/official-notice-reason.guard';
import { ReportableEmissionsComponent } from './submit/reportable-emissions/reportable-emissions.component';
import { ReportableEmissionsGuard } from './submit/reportable-emissions/reportable-emissions.guard';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';
import { SummaryGuard } from './submit/summary/summary.guard';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'DRE submit' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: NotifyOperatorComponent,
        canActivate: [NotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'peer-review',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canActivate: [PeerReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'determination-reason',
        data: { pageTitle: 'DRE submit determination reason' },
        component: DeterminationReasonComponent,
        canActivate: [DeterminationReasonGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'official-notice-reason',
        data: { pageTitle: 'DRE submit official notice reason', backlink: '../determination-reason' },
        component: OfficialNoticeReasonComponent,
        canActivate: [OfficialNoticeReasonGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'monitoring-approaches',
        data: { pageTitle: 'DRE submit monitoring approaches', backlink: '../official-notice-reason' },
        component: MonitoringApproachesComponent,
        canActivate: [MonitoringApproachesGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'reportable-emissions',
        data: { pageTitle: 'DRE submit reportable emissions', backlink: '../monitoring-approaches' },
        component: ReportableEmissionsComponent,
        canActivate: [ReportableEmissionsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'information-sources',
        children: [
          {
            path: '',
            data: { pageTitle: 'DRE submit information sources', backlink: '../reportable-emissions' },
            component: InformationSourcesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'DRE submit information source - create', backlink: '../' },
            component: InformationSourceComponent,
            canActivate: [InformationSourceGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index',
            children: [
              {
                path: '',
                data: { pageTitle: 'DRE submit information source', backlink: '../' },
                component: InformationSourceComponent,
                canActivate: [InformationSourceGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'delete',
                data: { pageTitle: 'DRE submit information source - Delete', backlink: '../..' },
                component: DeleteComponent,
                canActivate: [InformationSourceGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
        ],
        canActivate: [InformationSourcesGuard],
      },
      {
        path: 'charge-operator',
        data: { pageTitle: 'DRE submit charge operator', backlink: '../information-sources' },
        component: ChargeOperatorComponent,
        canActivate: [ChargeOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'fee',
        data: { pageTitle: 'DRE submit fee details', backlink: '../charge-operator' },
        component: FeeComponent,
        canActivate: [FeeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'DRE submit summary', breadcrumb: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'invalid-data',
        data: {
          pageTitle: 'Invalid data',
        },
        component: InvalidDataComponent,
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
      {
        path: 'summary',
        data: { pageTitle: 'DRE submit summary', breadcrumb: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
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
        component: DrePeerReview,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'summary',
        data: { pageTitle: 'DRE submit summary', breadcrumb: 'Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
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
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DreRoutingModule {}
