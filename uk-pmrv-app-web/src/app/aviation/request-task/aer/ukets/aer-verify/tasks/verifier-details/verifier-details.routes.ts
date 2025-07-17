import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateVerifierDetails, canDeactivateVerifierDetails } from './verifier-details.guard';

export const AER_VERIFIER_DETAILS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateVerifierDetails],
    canDeactivate: [canDeactivateVerifierDetails],
    children: [
      {
        path: '',
        data: { pageTitle: 'Verifier details' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./verifier-details-page/verifier-details-page.component'),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Verifier details' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./verifier-details-summary/verifier-details-summary.component'),
      },
    ],
  },
];
