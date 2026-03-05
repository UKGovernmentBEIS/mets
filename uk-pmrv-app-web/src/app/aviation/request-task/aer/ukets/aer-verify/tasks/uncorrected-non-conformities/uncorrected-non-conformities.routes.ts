import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivatePriorYearIssuesList,
  canActivateUncorrectedList,
  canActivateUncorrectedNonConformities,
  canDeactivateUncorrectedNonConformities,
} from './uncorrected-non-conformities.guard';

export const AER_UNCORRECTED_NON_CONFORMITIES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateUncorrectedNonConformities],
    canDeactivate: [canDeactivateUncorrectedNonConformities],
    children: [
      {
        path: '',
        data: { pageTitle: 'Uncorrected non-conformities' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./uncorrected-non-conformities-page/uncorrected-non-conformities-page.component'),
      },
      {
        path: 'uncorrected-list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Non-conformities with the approved emissions monitoring plan' },
            canActivate: [canActivateTaskForm, canActivateUncorrectedList],
            loadComponent: () =>
              import('./uncorrected-non-conformities-list/uncorrected-non-conformities-list.component'),
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a non-conformity with the approved emissions monitoring plan', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./uncorrected-non-conformities-item/uncorrected-non-conformities-item.component'),
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import('./uncorrected-non-conformities-item-delete/uncorrected-non-conformities-item-delete.component'),
          },
        ],
      },
      {
        path: 'prior-year-issues',
        data: { pageTitle: 'Non-conformities from the previous year not resolved' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './uncorrected-non-conformities-prior-year-issues/uncorrected-non-conformities-prior-year-issues.component'
          ),
      },
      {
        path: 'prior-year-issues-list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            data: { pageTitle: 'Non-conformities from the previous year not resolved' },
            canActivate: [canActivateTaskForm, canActivatePriorYearIssuesList],
            loadComponent: () =>
              import(
                './uncorrected-non-conformities-prior-year-list/uncorrected-non-conformities-prior-year-list.component'
              ),
          },
          {
            path: ':index',
            data: { pageTitle: 'Add a non-conformity from the previous year not resolved', backlink: '../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import(
                './uncorrected-non-conformities-prior-year-item/uncorrected-non-conformities-prior-year-item.component'
              ),
          },
          {
            path: ':index/delete',
            data: { pageTitle: 'Are you sure you want to delete this item?', backlink: '../../' },
            canActivate: [canActivateTaskForm],
            loadComponent: () =>
              import(
                './uncorrected-non-conformities-prior-year-item-delete/uncorrected-non-conformities-prior-year-item-delete.component'
              ),
          },
        ],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Uncorrected non-conformities' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./uncorrected-non-conformities-summary/uncorrected-non-conformities-summary.component'),
      },
    ],
  },
];
