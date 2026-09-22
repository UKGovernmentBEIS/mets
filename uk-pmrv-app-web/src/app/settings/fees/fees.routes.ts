import { Routes } from '@angular/router';

import { canExecuteSettingsFees } from '../core/settings-fees-permission.guard';
import { CancelScheduledChangeComponent } from './cancel-scheduled-change/cancel-scheduled-change.component';
import { ChangePaymentAmountComponent } from './change-payment-amount/change-payment-amount.component';
import { CheckYourAnswersComponent } from './check-your-answers/check-your-answers.component';
import { FeesComponent } from './fees.component';
import { FeesHistoryComponent } from './fees-history/fees-history.component';

export const FEES_ROUTES: Routes = [
  {
    path: '',
    component: FeesComponent,
    data: { pageTitle: 'Fees' },
  },
  {
    path: 'history',
    component: FeesHistoryComponent,
    data: { pageTitle: 'Fees history', breadcrumb: 'Fees history' },
  },
  {
    path: ':key/change',
    component: ChangePaymentAmountComponent,
    canMatch: [canExecuteSettingsFees()],
    data: { pageTitle: 'Change payment amount', breadcrumb: 'Change payment amount', backlink: '../..' },
  },
  {
    path: ':key/change/check-your-answers',
    component: CheckYourAnswersComponent,
    canMatch: [canExecuteSettingsFees()],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Check your answers' },
  },
  {
    path: ':key/cancel-change',
    component: CancelScheduledChangeComponent,
    canMatch: [canExecuteSettingsFees()],
    data: { pageTitle: 'Cancel scheduled change', breadcrumb: false, backlink: '../..' },
  },
];
