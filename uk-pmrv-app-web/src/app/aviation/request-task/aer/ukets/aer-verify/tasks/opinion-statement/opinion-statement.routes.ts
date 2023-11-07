import { Routes } from '@angular/router';

import { canActivateRequestTask, canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateOpinionStatement, canDeactivateOpinionStatement } from './opinion-statement.guard';

export const AER_OPINIOON_STATEMENT_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateOpinionStatement],
    canDeactivate: [canDeactivateOpinionStatement],
    children: [
      {
        path: '',
        data: { pageTitle: 'Review the aircraft operator’s emissions details' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./opinion-statement-page/opinion-statement-page.component'),
      },
      {
        path: 'changes-not-covered',
        data: {
          pageTitle: 'Other changes not covered in the approved emissions monitoring plans',
          breadcrumb: 'Changes not covered in the approved EMP',
          backlink: '../',
        },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./opinion-statement-changes-form/opinion-statement-changes-form.component'),
      },
      {
        path: 'site-visit',
        data: {
          backlink: '../changes-not-covered',
        },
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Site visit',
            },
            canActivate: [canActivateTaskForm],
            loadComponent: () => import('./opinion-statement-visit-form/opinion-statement-visit-form.component'),
          },
          {
            path: 'in-person',
            data: {
              pageTitle: 'In-person site visit details',
              backlink: '../',
            },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./opinion-statement-in-person-visit-form/opinion-statement-in-person-visit-form.component'),
          },
          {
            path: 'virtual',
            data: {
              pageTitle: 'Virtual site visit details',
              backlink: '../',
            },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./opinion-statement-virtual-site-visit-form/opinion-statement-virtual-site-visit-form.component'),
          },
        ],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check your answers',
          breadcrumb: 'Opinion statement',
        },
        canActivate: [canActivateSummaryPage, canActivateRequestTask],
        loadComponent: () => import('./opinion-statement-summary/opinion-statement-summary.component'),
      },
    ],
  },
];
