import { Routes } from '@angular/router';

import {
  canActivateVerifierDetails,
  canDeactivateVerifierDetails,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_VERIFIER_DETAILS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateVerifierDetails],
    canDeactivate: [canDeactivateVerifierDetails],
    children: [
      {
        path: '',
        data: { pageTitle: 'Complete verifier details' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./verifier-details.component').then((c) => c.VerifierDetailsComponent),
      },
      {
        path: 'conflict-interest',
        data: { pageTitle: 'Avoiding a conflict of interest', backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./conflict-interest/conflict-interest.component').then((c) => c.ConflictInterestComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Verifier details and impartiality summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
