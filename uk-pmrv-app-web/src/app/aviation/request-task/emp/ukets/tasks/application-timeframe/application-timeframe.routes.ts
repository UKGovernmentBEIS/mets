import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateApplicationTimeframe, canDeactivateApplicationTimeframe } from './application-timeframe.guards';

export const EMP_APPLICATION_TIMEFRAME_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateApplicationTimeframe],
    canDeactivate: [canDeactivateApplicationTimeframe],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./application-timeframe-apply/application-timeframe-apply.component').then(
            (c) => c.ApplicationTimeframeApplyComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Application timeframe' },
        loadComponent: () =>
          import('./application-timeframe-summary/application-timeframe-summary.component').then(
            (c) => c.ApplicationTimeframeSummaryComponent,
          ),
      },
    ],
  },
];
