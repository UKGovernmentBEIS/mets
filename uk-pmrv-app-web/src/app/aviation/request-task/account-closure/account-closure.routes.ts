import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestTaskStore } from '../store';
import { AccountClosureConfirmationComponent } from './account-closure-confirmation/account-closure-confirmation.component';
import { AccountClosureReasonComponent } from './account-closure-reason/account-closure-reason.component';
import { AccountClosureSubmitComponent } from './account-closure-submit/account-closure-submit.component';

const canDeactivateAccountClosure: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const ACCOUNT_CLOSURE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canDeactivate: [canDeactivateAccountClosure],
        component: AccountClosureReasonComponent,
      },
      {
        path: 'summary',
        canDeactivate: [canDeactivateAccountClosure],
        component: AccountClosureSubmitComponent,
      },
      {
        path: 'confirmation',
        component: AccountClosureConfirmationComponent,
      },
    ],
  },
];
