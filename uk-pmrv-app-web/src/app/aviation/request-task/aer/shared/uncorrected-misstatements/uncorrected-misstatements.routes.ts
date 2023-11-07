import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { MisstatementItemComponent } from './misstatement-item';
import { MisstatementItemDeleteComponent } from './misstatement-item-delete/misstatement-item-delete.component';
import { MisstatementListComponent } from './misstatement-list';
import {
  canActivateUncorrectedMisstatements,
  canDeactivateUncorrectedMisstatements,
} from './uncorrected-misstatements.guards';

export const AER_UNCORRECTED_MISSTATEMENTS_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'uncorrectedMisstatements' },
    canActivate: [canActivateUncorrectedMisstatements],
    canDeactivate: [canDeactivateUncorrectedMisstatements],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'Are there any misstatements that were not corrected before issuing this report?' },
        loadComponent: () =>
          import('./uncorrected-misstatements.component').then((c) => c.UncorrectedMisstatementsComponent),
      },
      {
        path: 'list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Misstatements not corrected before issuing this report', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: MisstatementListComponent,
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a misstatement not corrected before issuing this report', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: MisstatementItemComponent,
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            component: MisstatementItemDeleteComponent,
          },
        ],
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Uncorrected misstatements summary' },
        loadComponent: () =>
          import('./misstatements-summary/misstatements-summary.component').then(
            (c) => c.MisstatementsSummaryComponent,
          ),
      },
    ],
  },
];
