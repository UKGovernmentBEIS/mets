import { Routes } from '@angular/router';

import {
  canActivateEmissionsReductionClaim,
  canDeactivateEmissionsReductionClaim,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_EMISSIONS_REDUCTION_CLAIM_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateEmissionsReductionClaim],
    canDeactivate: [canDeactivateEmissionsReductionClaim],
    children: [
      {
        path: '',
        data: { pageTitle: 'Verify the emissions reduction claim' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim.component').then((c) => c.EmissionsReductionClaimComponent),
      },
      {
        path: 'operator',
        data: { pageTitle: 'CORSIA eligible fuels reduction claim' },
        loadComponent: () =>
          import('./operator/operator-emissions-reduction-claim.component').then(
            (c) => c.OperatorEmissionsReductionClaimComponent,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Verify the emissions reduction claim summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
