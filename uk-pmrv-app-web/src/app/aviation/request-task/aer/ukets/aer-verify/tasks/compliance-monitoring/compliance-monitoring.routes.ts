import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateComplianceMonitoring, canDeactivateComplianceMonitoring } from './compliance-monitoring.guard';

export const AER_COMPLIANCE_MONITORING_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'complianceMonitoringReportingRules' },
    canActivate: [canActivateComplianceMonitoring],
    canDeactivate: [canDeactivateComplianceMonitoring],
    children: [
      {
        path: '',
        data: { pageTitle: 'Compliance with the monitoring and reporting principles' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./compliance-monitoring-page.component'),
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check your answers',
          breadcrumb: 'Compliance with the monitoring and reporting principles summary',
        },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./compliance-monitoring-summary/compliance-monitoring-summary.component'),
      },
    ],
  },
];
