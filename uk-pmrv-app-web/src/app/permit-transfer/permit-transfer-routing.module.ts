import { inject, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DeterminationComponent } from '@permit-application/review/determination/determination.component';
import { DeterminationGuard } from '@permit-application/review/determination/determination.guard';
import { PermitTransferStore } from '@permit-transfer/store/permit-transfer.store';
import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';

import { TransferDetailsGuard } from './core/transfer-details.guard';
import { PermitTransferActionGuard } from './permit-transfer-action.guard';
import { PermitTransferTaskGuard } from './permit-transfer-task.guard';
import { TransferDecisionSummaryComponent } from './review/decision-summary/decision-summary.component';
import { ReviewSectionsContainerComponent } from './review/sections-container/review-sections-container.component';
import { TransferDetailsReviewComponent } from './review/transfer-details/transfer-details.component';
import { SectionsContainerComponent } from './sections-container/sections-container.component';
import { PermitTransferSummaryComponent } from './summary/summary.component';
import { TransferDetailsConfirmationSummaryComponent } from './transfer-details/summary/summary.component';
import { TransferDetailsComponent } from './transfer-details/transfer-details.component';

const routes: Routes = [
  {
    path: ':taskId',
    data: { breadcrumb: { resolveText: ({ type }) => new TaskTypeToBreadcrumbPipe().transform(type), skipLink: true} },
    resolve: { type: () => inject(PermitTransferStore).getState().requestTaskType },
    canActivate: [PermitTransferTaskGuard],
    canDeactivate: [PermitTransferTaskGuard],
    children: [
      {
        path: '',
        component: SectionsContainerComponent,
      },
      {
        path: 'transfer-details',
        children: [
          {
            path: '',
            data: { pageTitle: 'Transfer details' },
            component: TransferDetailsComponent,
            canActivate: [TransferDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            component: TransferDetailsConfirmationSummaryComponent,
            data: { pageTitle: 'Transfer details confirm your answers', breadcrumb: 'Transfer details' },
            canActivate: [TransferDetailsGuard],
          },
        ],
      },
      {
        path: 'review',
        children: [
          {
            path: '',
            component: ReviewSectionsContainerComponent,
          },
          {
            path: 'transfer-details',
            data: { pageTitle: 'Transfer details', groupKey: 'CONFIRM_TRANSFER_DETAILS', breadcrumb: true },
            component: TransferDetailsReviewComponent,
          },
          {
            path: 'determination',
            data: { pageTitle: 'Overall decision' },
            component: DeterminationComponent,
            canActivate: [DeterminationGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: '',
        loadChildren: () =>
          import('../permit-application/permit-application.module').then((m) => m.PermitApplicationModule),
      },
      {
        path: 'summary',
        component: PermitTransferSummaryComponent,
      },
    ],
  },
  {
    path: 'action/:actionId',
    children: [
      {
        path: '',
        component: SectionsContainerComponent,
      },
      {
        path: 'transfer-details',
        children: [
          {
            path: 'summary',
            component: TransferDetailsConfirmationSummaryComponent,
            canActivate: [TransferDetailsGuard],
          },
        ],
      },
      {
        path: 'review',
        children: [
          {
            path: '',
            data: { pageTitle: 'Permit determination' },
            component: ReviewSectionsContainerComponent,
          },
          {
            path: 'transfer-details',
            data: { pageTitle: 'Transfer details', groupKey: 'CONFIRM_TRANSFER_DETAILS' },
            component: TransferDetailsReviewComponent,
          },
          {
            path: 'decision-summary',
            data: { pageTitle: 'Transfer Decision Summary' },
            component: TransferDecisionSummaryComponent,
          },
        ],
      },
      {
        path: '',
        loadChildren: () =>
          import('../permit-application/permit-application.module').then((m) => m.PermitApplicationModule),
      },
    ],
    canActivate: [PermitTransferActionGuard],
    canDeactivate: [PermitTransferActionGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitTransferRoutingModule {}
