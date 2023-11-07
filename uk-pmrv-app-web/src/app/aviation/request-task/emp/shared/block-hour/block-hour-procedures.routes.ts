import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateBlockHourProcedures, canDeactivateBlockHourProcedures } from './block-hour-procedures.guards';

export const EMP_BLOCK_HOUR_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateBlockHourProcedures],
    canDeactivate: [canDeactivateBlockHourProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./specific-burn-fuel-calculation/specific-burn-fuel-calculation.component').then(
            (c) => c.SpecificBurnFuelCalculationComponent,
          ),
      },
      {
        path: 'block-hours-measurement',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./block-hours-measurement/block-hours-measurement.component').then(
            (c) => c.BlockHoursMeasurementComponent,
          ),
      },
      {
        path: 'fuel-uplift-supplier-records',
        data: { backlink: '../block-hours-measurement' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./fuel-uplift-supplier-records/fuel-uplift-supplier-records.component').then(
            (c) => c.FuelUpliftSupplierRecordsComponent,
          ),
      },
      {
        path: 'fuel-density',
        data: { backlink: '../fuel-uplift-supplier-records' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./fuel-density/fuel-density.component').then((c) => c.FuelDensityComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Block hour procedures summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./block-hour-summary/block-hour-summary.component').then((c) => c.BlockHourSummaryComponent),
      },
    ],
  },
];
