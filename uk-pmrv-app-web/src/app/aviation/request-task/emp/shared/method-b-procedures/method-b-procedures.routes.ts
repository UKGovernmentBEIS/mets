import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateMethodBProcedures, canDeactivateMethodBProcedures } from './method-b-procedures.guards';
export const EMP_METHOD_B_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMethodBProcedures],
    canDeactivate: [canDeactivateMethodBProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./method-b-procedures-fuel-consumption/method-b-procedures-fuel-consumption.component').then(
            (c) => c.MethodBProceduresFuelConsumptionComponent,
          ),
      },
      {
        path: 'fuel-density',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./method-b-procedures-fuel-density/method-b-procedures-fuel-density.component').then(
            (c) => c.MethodBProceduresFuelDensityComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Method B procedures summary' },
        loadComponent: () =>
          import('./method-b-procedures-summary/method-b-procedures-summary.component').then(
            (c) => c.MethodBProceduresSummaryComponent,
          ),
      },
    ],
  },
];
