import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateDoeAviationEmissions,
  canActivateDoeEmissionsChargesCalculate,
  canDeactivateDoeAviationEmissions,
} from './doe-aviation-emissions.guards';

export const DOE_AVIATION_EMISSIONS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateDoeAviationEmissions],
    canDeactivate: [canDeactivateDoeAviationEmissions],
    children: [
      {
        path: '',
        data: { pageTitle: 'Aviation emissions - Reason for estimating emissions' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./emissions-reasons').then((c) => c.DoeEmissionsReasonsComponent),
      },
      {
        path: 'estimated-emissions',
        data: { backlink: '../', pageTitle: 'Aviation emissions - Estimated or corrected emissions' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./estimated-emissions').then((c) => c.EstimatedEmissionsComponent),
      },
      {
        path: 'emission-charges',
        data: {
          backlink: '../estimated-emissions',
          pageTitle: 'Aviation emissions - Do you need to charge the operator a fee?',
        },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./emission-charges').then((c) => c.EmissionsChargesComponent),
      },
      {
        path: 'emission-charges-calculation',
        data: { backlink: '../emission-charges', pageTitle: 'Aviation emissions - Calculate the operator’s fee' },
        canActivate: [canActivateTaskForm, canActivateDoeEmissionsChargesCalculate],
        loadComponent: () =>
          import('./emission-charges-calculation').then((c) => c.EmissionsChargesCalculationComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Aviation emissions', pageTitle: 'Aviation emissions - Check your answers' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./emissions-summary').then((c) => c.DoeEmissionsSummaryComponent),
      },
    ],
  },
];
