import { Routes } from '@angular/router';

import {
  canActivateMonitoringApproach,
  canDeactivateMonitoringApproach,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_VERIFY_MONITORING_APPROACH_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMonitoringApproach],
    canDeactivate: [canDeactivateMonitoringApproach],
    children: [
      {
        path: '',
        data: { pageTitle: 'Confirm monitoring approach and emissions' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./monitoring-approach.component').then((c) => c.MonitoringApproachComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring approach and emissions summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
