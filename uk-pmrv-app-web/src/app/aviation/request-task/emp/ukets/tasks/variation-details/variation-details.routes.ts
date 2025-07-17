import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateVariationDetails, canDeactivateVariationDetails } from './variation-details.guards';

export const EMP_VARIATION_DETAILS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateVariationDetails],
    canDeactivate: [canDeactivateVariationDetails],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./variation-details-changes/variation-details-changes.component').then(
            (c) => c.VariationDetailsChangesComponent,
          ),
      },
      {
        path: 'reason',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./variation-details-reason/variation-details-reason.component').then(
            (c) => c.VariationDetailsReasonComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Describe the changes' },
        loadComponent: () =>
          import('./variation-details-summary/variation-details-summary.component').then(
            (c) => c.VariationDetailsSummaryComponent,
          ),
      },
    ],
  },
];
