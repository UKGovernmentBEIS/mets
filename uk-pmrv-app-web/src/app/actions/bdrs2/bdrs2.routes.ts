import { Routes } from '@angular/router';

import { Bdrs2BaselineSubmittedComponent } from './submitted/baseline/baseline-submitted.component';
import { Bdrs2SubmittedComponent } from './submitted/submitted.component';

export const BDRS2_ACTION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
  {
    path: 'submitted',
    children: [
      {
        path: '',
        data: { pageTitle: 'Stage 2 baseline data report submitted' },
        component: Bdrs2SubmittedComponent,
      },
      {
        path: 'baseline',
        data: { pageTitle: 'Stage 2 baseline data report', breadcrumb: true },
        component: Bdrs2BaselineSubmittedComponent,
      },
    ],
  },
];
