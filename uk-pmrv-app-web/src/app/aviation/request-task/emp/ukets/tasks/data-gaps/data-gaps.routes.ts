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
        path: 'substitute-data',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./substitute-data/substitute-data.component').then((c) => c.SubstituteDataComponent),
      },
      {
        path: 'other-data-gaps-types',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./other-data-gaps-types/other-data-gaps-types.component').then((c) => c.OtherDataGapsTypesComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Data gaps' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/data-gaps-summary.component').then((c) => c.DataGapsSummaryComponent),
      },
    ],
  },
];
