import { inject, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { CancelledComponent } from './actions/cancelled/cancelled.component';
import { CompletedComponent } from './actions/completed/completed.component';
import { PaidComponent } from './actions/paid/paid.component';
import { ReceivedComponent } from './actions/received/received.component';
import { getHeadingMap } from './core/payment.map';
import { PaymentRoute } from './core/payment-route.interface';
import { BankTransferComponent } from './make/bank-transfer/bank-transfer.component';
import { BankTransferGuard } from './make/bank-transfer/bank-transfer.guard';
import { ConfirmationComponent } from './make/confirmation/confirmation.component';
import { ConfirmationGuard } from './make/confirmation/confirmation.guard';
import { DetailsComponent } from './make/details/details.component';
import { MarkPaidComponent } from './make/mark-paid/mark-paid.component';
import { MarkPaidGuard } from './make/mark-paid/mark-paid.guard';
import { NotSuccessComponent } from './make/not-success/not-success.component';
import { OptionsComponent } from './make/options/options.component';
import { PaymentGuard } from './payment.guard';
import { PaymentActionGuard } from './payment-action.guard';
import { PaymentExistGuard } from './payment-exist.guard';
import { PaymentStore } from './store/payment.store';
import { CancelComponent } from './track/cancel/cancel.component';
import { MarkPaidComponent as TrackMarkPaidComponent } from './track/mark-paid/mark-paid.component';
import { TrackComponent } from './track/track.component';

const taskRoutes: PaymentRoute[] = [
  {
    path: 'make',
    data: {
      pageTitle: 'Make payment details',
      breadcrumb: ({ type, year }) => getHeadingMap(year)[type],
    },
    resolve: {
      type: () => inject(PaymentStore).getState().requestTaskItem?.requestTask.type,
      year: () => (inject(PaymentStore).getState().requestTaskItem?.requestInfo?.requestMetadata as any)?.year,
    },
    children: [
      {
        path: '',
        component: DetailsComponent,
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'options',
        data: { pageTitle: 'Make payment options', breadcrumb: true },
        component: OptionsComponent,
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'bank-transfer',
        data: { pageTitle: 'Make payment by bank transfer', breadcrumb: true },
        component: BankTransferComponent,
        canActivate: [BankTransferGuard],
      },
      {
        path: 'mark-paid',
        data: { pageTitle: 'Mark payment as paid', breadcrumb: true },
        component: MarkPaidComponent,
        canActivate: [MarkPaidGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Payment Confirmation', breadcrumb: true },
        component: ConfirmationComponent,
        canActivate: [ConfirmationGuard],
      },
      {
        path: 'not-success',
        data: { pageTitle: 'Payment not completed', breadcrumb: true },
        component: NotSuccessComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
  {
    path: 'track',
    data: {
      pageTitle: 'Payment tracking details',
      breadcrumb: ({ type, year }) => getHeadingMap(year)[type],
    },
    resolve: {
      type: () => inject(PaymentStore).getState().requestTaskItem?.requestTask.type,
      year: () => (inject(PaymentStore).getState().requestTaskItem?.requestInfo?.requestMetadata as any)?.year,
    },
    children: [
      {
        path: '',
        component: TrackComponent,
      },
      {
        path: 'mark-paid',
        data: { pageTitle: 'Mark payment as received', breadcrumb: true },
        component: TrackMarkPaidComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'cancel',
        data: { pageTitle: 'Cancel payment', breadcrumb: true },
        component: CancelComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },
];

const actionRoutes: PaymentRoute[] = [
  {
    path: 'paid',
    data: { pageTitle: 'Payment marked as paid', breadcrumb: true },
    component: PaidComponent,
  },
  {
    path: 'cancelled',
    data: { pageTitle: 'Payment task cancelled', breadcrumb: true },
    component: CancelledComponent,
  },
  {
    path: 'received',
    data: { pageTitle: 'Payment task received', breadcrumb: true },
    component: ReceivedComponent,
  },
  {
    path: 'completed',
    data: { pageTitle: 'Payment completed', breadcrumb: true },
    component: CompletedComponent,
  },
];

const routes: PaymentRoute[] = [
  {
    path: ':taskId',
    canActivate: [PaymentGuard],
    canDeactivate: [PaymentGuard],
    children: taskRoutes,
  },
  {
    path: 'actions/:actionId',
    canActivate: [PaymentActionGuard],
    canDeactivate: [PaymentActionGuard],
    children: actionRoutes,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PaymentRoutingModule {}
