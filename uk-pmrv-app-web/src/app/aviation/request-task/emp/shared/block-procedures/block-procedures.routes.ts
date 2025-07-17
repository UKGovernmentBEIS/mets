import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateBlockProcedures, canDeactivateBlockProcedures } from './block-procedures.guards';

export const EMP_BLOCK_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateBlockProcedures],
    canDeactivate: [canDeactivateBlockProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./block-procedures-monitoring/block-procedures-monitoring.component').then(
            (c) => c.BlockProceduresMonitoringComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Block procedures' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./block-procedures-monitoring-summary/block-procedures-monitoring-summary.component').then(
            (c) => c.BlockProceduresMonitoringSummaryComponent,
          ),
      },
    ],
  },
];
