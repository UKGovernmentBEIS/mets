import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';

const canDeactivateVir: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const VIR_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateVir],
    children: [
      {
        path: 'submit',
        loadChildren: () => import('./submit/vir-submit.routes').then((r) => r.VIR_SUBMIT_ROUTES),
      },
      {
        path: 'review',
        loadChildren: () => import('./review/vir-review.routes').then((r) => r.VIR_REVIEW_ROUTES),
      },
      {
        path: 'respond',
        loadChildren: () => import('./respond/vir-respond.routes').then((r) => r.VIR_RESPOND_ROUTES),
      },
    ],
  },
];
