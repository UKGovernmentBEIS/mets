import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateAviationAerCorsiaDataGaps,
  canActivateDataGapAdd,
  canActivateDataGapEdit,
  canActivateDataGapsList,
  canDeactivateAviationAerDataGaps,
} from './data-gaps.guards';

export const AER_CORSIA_DATA_GAPS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAviationAerCorsiaDataGaps],
    canDeactivate: [canDeactivateAviationAerDataGaps],
    children: [
      {
        path: '',
        canActivate: [canActivateAviationAerCorsiaDataGaps, canActivateTaskForm],
        loadComponent: () => import('./data-gaps-page/data-gaps-page.component'),
      },
      {
        path: 'threshold',
        data: { backlink: '../' },
        canActivate: [canActivateAviationAerCorsiaDataGaps, canActivateTaskForm],
        loadComponent: () => import('./threshold-page/threshold-page.component'),
      },
      {
        path: 'data-gaps-list',
        canActivate: [canActivateAviationAerCorsiaDataGaps, canActivateTaskForm],
        children: [
          {
            path: '',
            data: { backlink: '../threshold' },
            canActivate: [canActivateDataGapsList],
            loadComponent: () => import('./data-gaps-list/data-gaps-list.component'),
          },
          {
            path: 'add-data-gap-information',
            data: { backlink: '../../threshold' },
            canActivate: [canActivateDataGapAdd],
            loadComponent: () => import('./data-gaps-information/data-gaps-information-page.component'),
          },
          {
            path: ':index/edit',
            data: { backlink: '../../' },
            canActivate: [canActivateDataGapEdit],
            loadComponent: () => import('./data-gaps-information/data-gaps-information-page.component'),
          },
        ],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Data gaps  summary' },
        canActivate: [canActivateAviationAerCorsiaDataGaps, canActivateSummaryPage],
        loadComponent: () => import('./data-gaps-summary/data-gaps-summary.component'),
      },
    ],
  },
];
