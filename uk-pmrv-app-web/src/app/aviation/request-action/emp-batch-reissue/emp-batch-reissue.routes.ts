import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestActionStore } from '@aviation/request-action/store';

import { CompletedComponent as BatchReissueCompletedComponent } from './batch-reissue/completed/completed.component';
import { SubmittedComponent } from './batch-reissue/submitted/submitted.component';
import { CompletedComponent as ReissueCompletedComponent } from './reissue/completed/completed.component';

const canDeactivate: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

export const ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivate],
    children: [
      {
        path: 'batch-reissue',
        children: [
          {
            path: 'submitted',
            component: SubmittedComponent,
          },
          {
            path: 'completed',
            component: BatchReissueCompletedComponent,
          },
        ],
      },
      {
        path: 'reissue',
        children: [
          {
            path: 'completed',
            component: ReissueCompletedComponent,
          },
        ],
      },
    ],
  },
];
