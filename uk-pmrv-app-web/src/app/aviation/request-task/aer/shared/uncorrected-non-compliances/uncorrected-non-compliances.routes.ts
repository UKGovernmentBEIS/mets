import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { NonCompliancesItemComponent } from './non-compliances-item';
import { NonCompliancesItemDeleteComponent } from './non-compliances-item-delete/non-compliances-item-delete.component';
import { NonCompliancesListComponent } from './non-compliances-list/non-compliances-list.component';
import {
  canActivateUncorrectedNonCompliances,
  canDeactivateUncorrectedNonCompliances,
} from './uncorrected-non-compliances.guards';

export const AER_UNCORRECTED_NON_COMPLIANCES_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'uncorrectedNonCompliances' },
    canActivate: [canActivateUncorrectedNonCompliances],
    canDeactivate: [canDeactivateUncorrectedNonCompliances],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: {
          pageTitle: 'Have there been any uncorrected non-compliances with the monitoring and reporting regulations?',
        },
        loadComponent: () =>
          import('./uncorrected-non-compliances.component').then((c) => c.UncorrectedNonCompliancesComponent),
      },
      {
        path: 'list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Non-compliances with the monitoring and reporting regulations', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: NonCompliancesListComponent,
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a non-compliance with the monitoring and reporting regulations', backlink: '../' },
            canActivate: [canActivateTaskForm],
            component: NonCompliancesItemComponent,
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            component: NonCompliancesItemDeleteComponent,
          },
        ],
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Uncorrected non-compliances summary' },
        loadComponent: () =>
          import('./non-compliances-summary/non-compliances-summary.component').then(
            (c) => c.NonCompliancesSummaryComponent,
          ),
      },
    ],
  },
];
