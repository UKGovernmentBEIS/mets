import { Routes } from '@angular/router';

import {
  canActivateTotalEmissions,
  canActivateTotalEmissionsPage,
  canActivateTotalEmissionsSummaryPage,
} from '@aviation/request-task/aer/corsia/tasks/total-emissions/total-emissions.guard';

/**
 * Guard 'activateTaskForm', substituted by 'canActivateTotalEmissionsPage', since there is no an actual form.
 * Guard 'canActivateSummaryPage', substituted by 'canActivateTotalEmissionsSummaryPage', since there is no actual form.
 */
export const AER_CORSIA_TOTAL_EMISSIONS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateTotalEmissions],
    children: [
      {
        path: '',
        data: { pageTitle: 'Total emissions' },
        canActivate: [canActivateTotalEmissionsPage],
        loadComponent: () => import('./total-emissions-page').then((c) => c.TotalEmissionsPageComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Total emissions' },
        canActivate: [canActivateTotalEmissionsSummaryPage],
        loadComponent: () => import('./total-emissions-summary').then((c) => c.TotalEmissionsSummaryComponent),
      },
    ],
  },
];
