import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import {
  canActivateMonitoringApproach,
  canActivateSchemeYearTotalEmissions,
  canActivateTotalNumberFullScopeFlights,
  canDeactivateMonitoringApproach,
} from './monitoring-approach.guards';

export const AER_MONITORING_APPROACH_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMonitoringApproach],
    canDeactivate: [canDeactivateMonitoringApproach],
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
        path: 'scheme-year-total-emissions',
        canActivate: [canActivateTaskForm, canActivateSchemeYearTotalEmissions],
        loadComponent: () =>
          import('./monitoring-approach-total-emissions').then((c) => c.MonitoringApproachTotalEmissionsComponent),
      },
      {
        path: 'total-number-full-scope-flights',
        canActivate: [canActivateTaskForm, canActivateTotalNumberFullScopeFlights],
        loadComponent: () =>
          import('./monitoring-approach-number-flights').then((c) => c.MonitoringApproachNumberFlightsComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring approach' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./monitoring-approach-summary').then((c) => c.MonitoringApproachSummaryComponent),
      },
    ],
  },
];
