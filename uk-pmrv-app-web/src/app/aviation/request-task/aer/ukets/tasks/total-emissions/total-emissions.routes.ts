import { Routes } from '@angular/router';

import {
  canActivateTotalEmissions,
  canDeactivateTotalEmissions,
} from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions.guard';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';

export const AER_TOTAL_EMISSIONS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateTotalEmissions],
    canDeactivate: [canDeactivateTotalEmissions],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./total-emissions-page').then((c) => c.TotalEmissionsPageComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Total emissions', breadcrumb: true },
        loadComponent: () => import('./total-emissions-summary').then((c) => c.TotalEmissionsSummaryComponent),
      },
    ],
  },
];
