import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateAviationEmissions,
  canActivateaviationEmissionsChargesCalculate,
  canDeactivateAviationEmissions,
} from './aviation-emissions.guards';

export const DRE_AVIATION_EMISSIONS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAviationEmissions],
    canDeactivate: [canDeactivateAviationEmissions],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./aviation-emissions-reasons').then((c) => c.AviationEmissionsReasonsComponent),
      },
      {
        path: 'aviation-emissions',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./aviation-emissions').then((c) => c.AviationEmissionsComponent),
      },
      {
        path: 'aviation-emissions-charges',
        data: { backlink: '../aviation-emissions' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./aviation-emissions-charges').then((c) => c.AviationEmissionsChargesComponent),
      },
      {
        path: 'aviation-emissions-charges-calculate',
        data: { backlink: '../aviation-emissions-charges' },
        canActivate: [canActivateTaskForm, canActivateaviationEmissionsChargesCalculate],
        loadComponent: () =>
          import('./aviation-emissions-charges-calculate').then((c) => c.AviationEmissionsChargesCalculateComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Aviation emissions' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./aviation-emissions-summary').then((c) => c.AviationEmissionsSummaryComponent),
      },
    ],
  },
];
