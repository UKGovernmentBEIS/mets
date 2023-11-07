import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateDataGaps, canDeactivateDataGaps } from './data-gaps.guard';

export const EMP_DATA_GAPS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateDataGaps],
    canDeactivate: [canDeactivateDataGaps],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./data-gaps-form/data-gaps-form.component').then((c) => c.DataGapsFormComponent),
      },
      {
        path: 'secondary-data-sources',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./secondary-data-sources/secondary-data-sources.component').then(
            (c) => c.SecondaryDataSourcesComponent,
          ),
      },
      {
        path: 'secondary-data-sources-exist',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./secondary-data-sources-exist/secondary-data-sources-exist.component').then(
            (c) => c.SecondaryDataSourcesExistComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/data-gaps-summary.component').then((c) => c.DataGapsSummaryComponent),
      },
    ],
  },
];
