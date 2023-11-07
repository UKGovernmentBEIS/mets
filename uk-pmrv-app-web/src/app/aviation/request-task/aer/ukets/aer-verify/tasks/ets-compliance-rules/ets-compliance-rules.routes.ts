import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateEtsComplianceRules, canDeactivateEtsComplianceRules } from './ets-compliance-rules.guard';

export const AER_ETS_COMPLIANCE_RULES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateEtsComplianceRules],
    canDeactivate: [canDeactivateEtsComplianceRules],
    children: [
      {
        path: '',
        data: { pageTitle: 'Compliance with ETS rules' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./ets-compliance-rules-page/ets-compliance-rules-page.component'),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Compliance with ETS rules' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./ets-compliance-rules-summary/ets-compliance-rules-summary.component'),
      },
    ],
  },
];
