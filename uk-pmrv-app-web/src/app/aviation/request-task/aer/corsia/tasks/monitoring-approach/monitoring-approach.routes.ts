import { Routes } from '@angular/router';

import { canActivateRequestTask, canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateCertUsage,
  canActivateFuelAllocationBlockHour,
  canActivateFuelUsage,
  canActivateMonitoringApproach,
  canDeactivateMonitoringApproach,
} from './monitoring-approach.guards';

export const AER_CORSIA_MONITORING_APPROACH_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMonitoringApproach],
    canDeactivate: [canDeactivateMonitoringApproach],
    canActivateChild: [canActivateMonitoringApproach],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'CERT' },
        loadComponent: () =>
          import('./monitoring-approach-cert/monitoring-approach-cert.component').then(
            (c) => c.MonitoringApproachCertComponent,
          ),
      },
      {
        path: 'cert-usage',
        canActivate: [canActivateTaskForm, canActivateCertUsage],
        data: { pageTitle: 'CERT usage', backlink: '../' },
        loadComponent: () =>
          import('./monitoring-approach-cert-usage').then((c) => c.MonitoringApproachCertUsageComponent),
      },
      {
        path: 'fuel-usage',
        canActivate: [canActivateTaskForm, canActivateFuelUsage],
        data: { pageTitle: 'Fuel usage', backlink: '../' },
        loadComponent: () =>
          import('./monitoring-approach-fuel-usage').then((c) => c.MonitoringApproachFuelUsageComponent),
      },
      {
        path: 'fuel-allocation-block-hour',
        data: { pageTitle: 'Fuel allocation block hour', backlink: '../fuel-usage' },
        canActivate: [canActivateTaskForm, canActivateFuelAllocationBlockHour],
        loadComponent: () =>
          import('./monitoring-approach-fuel-allocation-block-hour').then(
            (c) => c.MonitoringApproachFuelAllocationBlockHourComponent,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring approach' },
        canActivate: [canActivateRequestTask, canActivateSummaryPage],
        loadComponent: () => import('./monitoring-approach-summary').then((c) => c.MonitoringApproachSummaryComponent),
      },
    ],
  },
];
