import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { CancelledComponent } from './actions/cancelled/cancelled.component';
import { CompletedComponent } from './actions/completed/completed.component';
import { PaidComponent } from './actions/paid/paid.component';
import { ReceivedComponent } from './actions/received/received.component';
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
import { CancelComponent } from './track/cancel/cancel.component';
import { MarkPaidComponent as TrackMarkPaidComponent } from './track/mark-paid/mark-paid.component';
import { TrackComponent } from './track/track.component';

const taskRoutes: PaymentRoute[] = [
  {
    path: 'make',
    children: [
      {
        path: 'details',
        component: DetailsComponent,
        data: { pageTitle: 'Make payment details' },
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'options',
        component: OptionsComponent,
        data: { pageTitle: 'Make payment options' },
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'bank-transfer',
        component: BankTransferComponent,
        data: { pageTitle: 'Make payment by bank transfer' },
        canActivate: [BankTransferGuard],
      },
      {
        path: 'mark-paid',
        component: MarkPaidComponent,
        data: { pageTitle: 'Mark payment as paid' },
        canActivate: [MarkPaidGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: ConfirmationComponent,
        data: { pageTitle: 'Payment Confirmation' },
        canActivate: [ConfirmationGuard],
      },
      {
        path: 'not-success',
        component: NotSuccessComponent,
        data: { pageTitle: 'Payment not completed' },
      },
    ],
  },
  {
    path: 'track',
    children: [
      {
        path: '',
        component: TrackComponent,
        data: { pageTitle: 'Payment tracking details' },
      },
      {
        path: 'mark-paid',
        component: TrackMarkPaidComponent,
        data: { pageTitle: 'Mark payment as received' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'cancel',
        component: CancelComponent,
        data: { pageTitle: 'Cancel payment' },
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
    component: PaidComponent,
    data: { pageTitle: 'Payment marked as paid', breadcrumb: true },
  },
  {
    path: 'cancelled',
    component: CancelledComponent,
    data: { pageTitle: 'Payment task cancelled', breadcrumb: true },
  },
  {
    path: 'received',
    component: ReceivedComponent,
    data: { pageTitle: 'Payment task received', breadcrumb: true },
  },
  {
    path: 'completed',
    component: CompletedComponent,
    data: { pageTitle: 'Payment completed', breadcrumb: true },
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
