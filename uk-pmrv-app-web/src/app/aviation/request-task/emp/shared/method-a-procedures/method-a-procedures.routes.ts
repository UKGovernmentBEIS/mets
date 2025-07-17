import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateMethodAProcedures, canDeactivateMethodAProcedures } from './method-a-procedures.guards';
export const EMP_METHOD_A_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMethodAProcedures],
    canDeactivate: [canDeactivateMethodAProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./method-a-procedures-fuel-consumption/method-a-procedures-fuel-consumption.component').then(
            (c) => c.MethodAProceduresFuelConsumptionComponent,
          ),
      },
      {
        path: 'fuel-density',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./method-a-procedures-fuel-density/method-a-procedures-fuel-density.component').then(
            (c) => c.MethodAProceduresFuelDensityComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Method A procedures' },
        loadComponent: () =>
          import('./method-a-procedures-summary/method-a-procedures-summary.component').then(
            (c) => c.MethodAProceduresSummaryComponent,
          ),
      },
    ],
  },
];
