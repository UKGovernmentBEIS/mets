import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateAerEmissionsReductionClaim,
  canDeactivateAerEmissionsReductionClaim,
} from './emissions-reduction-claim.guard';

export const AER_EMISSIONS_REDUCTION_CLAIM_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAerEmissionsReductionClaim],
    canDeactivate: [canDeactivateAerEmissionsReductionClaim],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-guard-page').then((c) => c.EmissionsReductionClaimGuardPageComponent),
      },
      {
        path: 'add-batch-item',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-add-batch-item').then(
            (c) => c.EmissionsReductionClaimAddBatchItemComponent,
          ),
      },
      {
        path: 'remove-batch-item',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-remove-batch-item').then(
            (c) => c.EmissionsReductionClaimRemoveBatchItemComponent,
          ),
      },
      {
        path: 'list',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-list').then((c) => c.EmissionsReductionClaimListComponent),
      },
      {
        path: 'declaration',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-declaration').then((c) => c.EmissionsReductionClaimDeclarationComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./emissions-reduction-claim-summary').then((c) => c.EmissionsReductionClaimSummaryComponent),
      },
    ],
  },
];
