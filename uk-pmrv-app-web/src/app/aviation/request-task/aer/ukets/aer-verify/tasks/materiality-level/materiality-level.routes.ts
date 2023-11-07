import { Routes } from '@angular/router';

import {
  canActivateMaterialityLevel,
  canDeactivateMaterialityLevel,
} from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/materiality-level.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_MATERIALITY_LEVEL_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateMaterialityLevel],
    canDeactivate: [canDeactivateMaterialityLevel],
    children: [
      {
        path: '',
        data: { pageTitle: 'Materiality level' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./materiality-level-page/materiality-level-page.component'),
      },
      {
        path: 'reference-documents',
        data: { pageTitle: 'Select the reference documents that are appropriate to the accreditation you hold' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./materiality-level-reference-documents/materiality-level-reference-documents.component'),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Materiality level' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./materiality-level-summary/materiality-level-summary.component'),
      },
    ],
  },
];
