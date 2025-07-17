import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateMonitoringApproachCorsia,
  canActivateSimplifiedApproach,
  canDeactivateMonitoringApproachCorsia,
} from './monitoring-approach.guards';

export const EMP_MONITORING_APPROACH_CORSIA_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMonitoringApproachCorsia],
    canDeactivate: [canDeactivateMonitoringApproachCorsia],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./monitoring-approach-type/monitoring-approach-type.component').then(
            (c) => c.MonitoringApproachTypeComponent,
          ),
      },
      {
        path: 'simplified-approach',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm, canActivateSimplifiedApproach],
        loadComponent: () =>
          import('./simplified-approach/simplified-approach.component').then((c) => c.SimplifiedApproachComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Monitoring approach' },
        loadComponent: () =>
          import('./monitoring-summary/monitoring-summary.component').then((c) => c.MonitoringSummaryComponent),
      },
    ],
  },
];
