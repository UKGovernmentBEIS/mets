import { inject, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { canSkipReview } from '@aviation/request-task/guards/skip-review.guard';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';

import { RequestTaskStore } from '../request-task/store';
import { TYPE_AWARE_STORE } from '../type-aware.store';
import {
  AviationRecallFromAmendsComponent,
  CancelComponent,
  ConfirmationComponent,
  PaymentNotCompletedComponent,
  RequestTaskPageComponent,
  SkipReviewComponent,
  SkipReviewConfirmationComponent,
} from './containers';
import { CompleteReportConfirmationComponent } from './containers/complete-report/complete-report-confirmation/complete-report-confirmation.component';
import CompleteReportPageComponent from './containers/complete-report/complete-report-page/complete-report-page.component';
import { canActivateRequestTask, canDeactivateRequestTask, ChangeAssigneeGuard, RecallFromAmendsGuard } from './guards';
import { canCancelTask } from './guards/cancel-task.guard';
import { CompleteReportGuard } from './guards/complete-report-page.guard';
import { RecallReportFromVerifierGuard } from './guards/recall-report-from-verifier.guard';

const routes: Routes = [
  {
    path: '',
    providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    children: [
      {
        path: ':taskId/file-download/:fileType/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: ':taskId',
        data: { breadcrumb: ({ type }) => new TaskTypeToBreadcrumbPipe().transform(type) },
        resolve: { type: () => inject(RequestTaskStore).getState().requestTaskItem.requestTask.type },
        canActivate: [canActivateRequestTask],
        canDeactivate: [canDeactivateRequestTask],
        children: [
          {
            path: 'cancel',
            children: [
              {
                path: '',
                component: CancelComponent,
                data: { pageTitle: 'Task cancellation', breadcrumb: true },
                canActivate: [canCancelTask],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'confirmation',
                component: ConfirmationComponent,
                data: { pageTitle: 'Task cancellation confirmation' },
              },
            ],
          },
          {
            path: 'skip-review',
            children: [
              {
                path: '',
                component: SkipReviewComponent,
                data: { pageTitle: 'Skip review', breadcrumb: true, backlink: '../' },
                canActivate: [canSkipReview],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'confirmation',
                component: SkipReviewConfirmationComponent,
                data: { pageTitle: 'Skip review confirmation' },
              },
            ],
          },
          {
            path: 'account-closure',
            loadChildren: () =>
              import('./account-closure/account-closure.routes').then((r) => r.ACCOUNT_CLOSURE_ROUTES),
          },
          {
            path: 'change-assignee',
            canActivate: [ChangeAssigneeGuard],
            canDeactivate: [ChangeAssigneeGuard],
            loadChildren: () =>
              import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
          },
          {
            path: 'emp',
            loadChildren: () => import('./emp/ukets/emp.routes').then((r) => r.EMP_ROUTES),
          },
          {
            path: 'emp-corsia',
            loadChildren: () => import('./emp/corsia/emp.routes').then((r) => r.EMP_CORSIA_ROUTES),
          },
          {
            path: 'aer',
            loadChildren: () => import('./aer/ukets/aer-ukets.routes').then((r) => r.AER_UK_ETS_ROUTES),
          },
          {
            path: 'aer-corsia',
            loadChildren: () => import('./aer/corsia/aer-corsia.routes').then((r) => r.AER_CORSIA_ROUTES),
          },
          {
            path: 'aer-verify',
            loadChildren: () =>
              import('@aviation/request-task/aer/ukets/aer-verify/aer-verify.routes').then((r) => r.AER_VERIFY_ROUTES),
          },
          {
            path: 'aer-verify-corsia',
            loadChildren: () =>
              import('@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.routes').then(
                (r) => r.AER_VERIFY_CORSIA_ROUTES,
              ),
          },
          {
            path: 'vir',
            loadChildren: () => import('@aviation/request-task/vir/vir.routes').then((r) => r.VIR_ROUTES),
          },
          {
            path: 'non-compliance',
            loadChildren: () => import('./non-compliance/non-compliance.routes').then((r) => r.NON_COMPLIANCE_ROUTES),
          },
          {
            path: 'payment-not-completed',
            data: { breadcrumb: 'Payment not completed' },
            component: PaymentNotCompletedComponent,
          },
          {
            path: 'recall-from-amends',
            data: { breadcrumb: 'Recall from amends' },
            component: AviationRecallFromAmendsComponent,
            canActivate: [RecallFromAmendsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'recall-report-from-verifier',
            data: { breadcrumb: 'Recall report from verifier' },
            loadComponent: () =>
              import('./containers/recall-report-from-verifier/recall-report-from-verifier.component'),
            canActivate: [RecallReportFromVerifierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'dre',
            loadChildren: () => import('./dre/dre.routes').then((r) => r.DRE_ROUTES),
          },
          {
            path: 'peer-review-decision',
            children: [
              {
                path: '',
                data: { pageTitle: 'Peer review decision', breadcrumb: true },
                component: PeerReviewDecisionComponent,
                providers: [PeerReviewDecisionGuard],
                canActivate: [PeerReviewDecisionGuard],
              },
              {
                path: 'answers',
                data: { pageTitle: 'Peer review decision answers', breadcrumb: true },
                component: PeerReviewDecisionAnswersComponent,
                providers: [PeerReviewDecisionAnswersGuard],
                canActivate: [PeerReviewDecisionAnswersGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: 'confirmation',
                data: { pageTitle: 'Peer review decision confirmation', breadcrumb: true },
                component: PeerReviewDecisionConfirmationComponent,
              },
            ],
          },
          {
            path: 'complete-report',
            children: [
              {
                path: '',
                data: { pageTitle: 'Complete report', breadcrumb: true, backlink: '../' },
                component: CompleteReportPageComponent,
                canActivate: [CompleteReportGuard],
              },
              {
                path: 'complete-report-confirmation',
                data: { pageTitle: 'Complete report confirmation', breadcrumb: true },
                component: CompleteReportConfirmationComponent,
              },
            ],
          },
          {
            path: '',
            component: RequestTaskPageComponent,
          },
        ],
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: '/aviation/dashboard',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestTaskRoutingModule {}
