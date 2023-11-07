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
        canActivate: [canActivateTotalEmissionsPage],
        data: { pageTitle: 'Total emissions' },
        loadComponent: () => import('./total-emissions-page').then((c) => c.TotalEmissionsPageComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateTotalEmissionsSummaryPage],
        data: { pageTitle: 'Total emissions summary', breadcrumb: true },
        loadComponent: () => import('./total-emissions-summary').then((c) => c.TotalEmissionsSummaryComponent),
      },
    ],
  },
];
