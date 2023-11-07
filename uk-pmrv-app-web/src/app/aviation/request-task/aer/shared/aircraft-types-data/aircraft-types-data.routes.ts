import { Routes } from '@angular/router';

import { canActivateRequestTask, canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateAircraftTypesData, canDeactivateAircraftTypesData } from './aircraft-types-data.guards';

export const AER_AIRCRAFT_TYPES_DATA_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAircraftTypesData, canActivateRequestTask],
    canDeactivate: [canDeactivateAircraftTypesData],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./aircraft-types-data-page').then((c) => c.AircraftTypesDataPageComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Aircraft types data' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./aircraft-types-data-summary').then((c) => c.AircraftTypesDataSummaryComponent),
      },
    ],
  },
];
