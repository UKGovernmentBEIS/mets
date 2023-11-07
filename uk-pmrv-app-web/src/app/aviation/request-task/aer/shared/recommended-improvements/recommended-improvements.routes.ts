import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateRecommendedImprovements,
  canDeactivateRecommendedImprovements,
} from './recommended-improvements.guards';
import { RecommendedImprovementsItemComponent } from './recommended-improvements-item';
import { RecommendedImprovementsItemDeleteComponent } from './recommended-improvements-item-delete';
import { RecommendedImprovementsListComponent } from './recommended-improvements-list';

export const AER_RECOMMENDED_IMPROVEMENTS_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'recommendedImprovements' },
    canActivate: [canActivateRecommendedImprovements],
    canDeactivate: [canDeactivateRecommendedImprovements],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./recommended-improvements.component').then((c) => c.RecommendedImprovementsComponent),
      },
      {
        path: 'list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Recommended improvements', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: RecommendedImprovementsListComponent,
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a recommended improvement', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: RecommendedImprovementsItemComponent,
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            component: RecommendedImprovementsItemDeleteComponent,
          },
        ],
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Recommended improvements summary' },
        loadComponent: () =>
          import('./recommended-improvements-summary/recommended-improvements-summary.component').then(
            (c) => c.RecommendedImprovementsSummaryComponent,
          ),
      },
    ],
  },
];
