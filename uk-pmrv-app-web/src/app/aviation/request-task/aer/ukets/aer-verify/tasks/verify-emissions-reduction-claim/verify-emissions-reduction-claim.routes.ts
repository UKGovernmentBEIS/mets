import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateVerifyEmissionsReductionClaim,
  canDeactivateVerifyEmissionsReductionClaim,
} from './verify-emissions-reduction-claim.guard';

export const AER_VERIFY_EMISSIONS_REDUCTION_CLAIM_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateVerifyEmissionsReductionClaim],
    canDeactivate: [canDeactivateVerifyEmissionsReductionClaim],
    children: [
      {
        path: '',
        data: { pageTitle: 'Verify the emissions reduction claim (ERC)' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./verify-emissions-reduction-claim-page/verify-emissions-reduction-claim-page.component'),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Verify the emissions reduction claim (ERC)' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./verify-emissions-reduction-claim-summary/verify-emissions-reduction-claim-summary.component'),
      },
    ],
  },
];
