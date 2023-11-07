import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../guards';
import { canActivateReportingObligation, canDeactivateReportingObligation } from './reporting-obligation.guards';

export const AER_REPORTING_OBLIGATION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateReportingObligation],
    canDeactivate: [canDeactivateReportingObligation],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./reporting-obligation-page/reporting-obligation-page.component'),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Reporting obligation' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./reporting-obligation-summary/reporting-obligation-summary.component'),
      },
    ],
  },
];
