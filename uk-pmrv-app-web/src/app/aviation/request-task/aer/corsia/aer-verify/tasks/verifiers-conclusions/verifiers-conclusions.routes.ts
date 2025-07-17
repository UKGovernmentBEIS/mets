import { Routes } from '@angular/router';

import {
  canActivateVerifiersConclusions,
  canDeactivateVerifiersConclusions,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifiers-conclusions/verifiers-conclusions.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_VERIFIERS_CONCLUSIONS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateVerifiersConclusions],
    canDeactivate: [canDeactivateVerifiersConclusions],
    children: [
      {
        path: '',
        data: { pageTitle: 'Data quality and materiality' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./verifiers-conclusions.component').then((c) => c.VerifiersConclusionsComponent),
      },
      {
        path: 'emissions-report',
        data: { pageTitle: `Conclusion relating to the operator's emissions report`, backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-report/emissions-report.component').then((c) => c.EmissionsReportComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Conclusions on data quality and materiality' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
