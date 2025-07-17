import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateAviationAerDataGaps,
  canActivateDataGapAdd,
  canActivateDataGapEdit,
  canActivateDataGapsList,
  canDeactivateAviationAerDataGaps,
} from './data-gaps.guard';

export const AER_DATA_GAPS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAviationAerDataGaps],
    canDeactivate: [canDeactivateAviationAerDataGaps],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./data-gaps-page/data-gaps-page.component'),
      },
      {
        path: 'data-gaps-list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            canActivate: [canActivateDataGapsList],
            loadComponent: () => import('./data-gaps-list/data-gaps-list.component'),
          },
          {
            path: 'add-data-gap-information',
            data: { backlink: '../../' },
            canActivate: [canActivateDataGapAdd],
            loadComponent: () => import('./data-gaps-information/data-gaps-information.component'),
          },
          {
            path: ':index/edit',
            data: { backlink: '../../' },
            canActivate: [canActivateDataGapEdit],
            loadComponent: () => import('./data-gaps-information/data-gaps-information.component'),
          },
        ],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Data gaps' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./data-gaps-summary/data-gaps-summary.component'),
      },
    ],
  },
];
