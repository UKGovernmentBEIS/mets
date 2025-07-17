import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateFuelUpliftProcedures, canDeactivateFuelUpliftProcedures } from './fuel-uplift-procedures.guards';

export const EMP_FUEL_UPLIFT_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateFuelUpliftProcedures],
    canDeactivate: [canDeactivateFuelUpliftProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./fuel-uplift-measurement/fuel-uplift-measurement.component').then(
            (c) => c.FuelUpliftMeasurementComponent,
          ),
      },
      {
        path: 'assignment-and-adjustment',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./fuel-uplift-assignment/fuel-uplift-assignment.component').then(
            (c) => c.FuelUpliftAssignmentComponent,
          ),
      },
      {
        path: 'fuel-uplift-records',
        data: { backlink: '../assignment-and-adjustment' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./fuel-uplift-records/fuel-uplift-records.component').then((c) => c.FuelUpliftRecordsComponent),
      },
      {
        path: 'fuel-uplift-density',
        data: { backlink: '../fuel-uplift-records' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./fuel-uplift-density/fuel-uplift-density.component').then((c) => c.FuelUpliftDensityComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Fuel uplift procedures' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./fuel-uplift-summary/fuel-uplift-summary.component').then((c) => c.FuelUpliftSummaryComponent),
      },
    ],
  },
];
