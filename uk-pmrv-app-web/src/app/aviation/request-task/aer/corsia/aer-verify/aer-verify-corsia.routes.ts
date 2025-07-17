import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { AER_CORSIA_CHILD_ROUTES } from '@aviation/request-task/aer/corsia/shared/aer-corsia-child.routes';
import { AER_VERIFY_CORSIA_CHILD_ROUTES } from '@aviation/request-task/aer/corsia/shared/aer-verify-corsia-child.routes';
import { RequestTaskStore } from '@aviation/request-task/store';

const canDeactivateAerVerifyCorsia: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const AER_VERIFY_CORSIA_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAerVerifyCorsia],
    children: [...AER_VERIFY_CORSIA_CHILD_ROUTES, ...AER_CORSIA_CHILD_ROUTES],
  },
];
