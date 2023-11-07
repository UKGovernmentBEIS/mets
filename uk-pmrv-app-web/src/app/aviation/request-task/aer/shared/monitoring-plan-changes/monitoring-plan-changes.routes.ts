import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateMonitoringPlanChanges, canDeactivateMonitoringPlanChanges } from './monitoring-plan-changes.guards';

export const AER_MONITORING_PLAN_CHANGES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMonitoringPlanChanges],
    canDeactivate: [canDeactivateMonitoringPlanChanges],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./monitoring-plan-changes-page').then((c) => c.MonitoringPlanChangesPageComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Monitoring plan changes' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./monitoring-plan-changes-summary').then((c) => c.MonitoringPlanChangesSummaryComponent),
      },
    ],
  },
];
