import { Routes } from '@angular/router';

import {
  canActivateUncorrectedNonConformities,
  canDeactivateUncorrectedNonConformities,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_UNCORRECTED_NON_COMFORMITIES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateUncorrectedNonConformities],
    canDeactivate: [canDeactivateUncorrectedNonConformities],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'Uncorrected non-conformities' },
        loadComponent: () =>
          import('./uncorrected-non-conformities.component').then((c) => c.UncorrectedNonConformitiesComponent),
      },
      {
        path: 'list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Non-conformities with the approved monitoring plan', backlink: '../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./conformity-list/conformity-list.component').then((c) => c.ConformityListComponent),
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a non-conformity', backlink: '../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./conformity-item/conformity-item.component').then((c) => c.ConformityItemComponent),
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./conformity-item-delete/conformity-item-delete.component').then(
                (c) => c.ConformityItemDeleteComponent,
              ),
          },
        ],
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Uncorrected non-conformities summary' },
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
