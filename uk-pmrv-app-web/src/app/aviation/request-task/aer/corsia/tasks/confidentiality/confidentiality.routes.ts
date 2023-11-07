import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateConfidentiality, canDeactivateConfidentiality } from './confidentiality.guard';

export const AER_CORSIA_CONFIDENTIALITY_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateConfidentiality],
    canActivateChild: [canActivateConfidentiality],
    canDeactivate: [canDeactivateConfidentiality],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'Request for data not to be published by ICAO', breadcrumb: 'Confidentiality statement' },
        loadComponent: () =>
          import('./confidentiality-page/confidentiality-page.component').then((c) => c.ConfidentialityPageComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Confidentiality statement summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./confidentiality-summary/confidentiality-summary.component').then(
            (c) => c.ConfidentialitySummaryComponent,
          ),
      },
    ],
  },
];
