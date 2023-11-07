import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateOverallDecision, canDeactivateOverallDecision } from './overall-decision.guards';
import { ReasonItemComponent } from './reason-list/reason-item/reason-item.component';
import { ReasonItemDeleteComponent } from './reason-list/reason-item-delete';
import { ReasonListComponent } from './reason-list/reason-list.component';

export const AER_OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'overallDecision' },
    canActivate: [canActivateOverallDecision],
    canDeactivate: [canDeactivateOverallDecision],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'What is your assessment of this report?' },
        loadComponent: () => import('./overall-decision-page').then((c) => c.OverallDecisionComponent),
      },

      {
        path: 'not-verified',
        canActivate: [canActivateTaskForm],
        data: { pageTitle: 'Why can you not verify the report?', backlink: '../' },
        loadComponent: () => import('./not-verified/not-verified.component').then((c) => c.NotVerifiedComponent),
      },
      {
        path: 'reason-list',
        canActivate: [canActivateTaskForm],
        children: [
          {
            path: '',
            canActivate: [canActivateTaskForm],
            data: { pageTitle: 'List the reasons for your decision', backlink: '../' },
            component: ReasonListComponent,
          },
          {
            path: ':index',
            canActivate: [canActivateTaskForm],
            data: { pageTitle: 'Add a reason', backlink: '../' },
            component: ReasonItemComponent,
          },
          {
            path: ':index/delete',
            canActivate: [canActivateTaskForm],
            data: { pageTitle: 'Are you sure you want to delete this item', backlink: '../..' },
            component: ReasonItemDeleteComponent,
          },
        ],
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Overall decision summary' },
        loadComponent: () =>
          import('./summary/overall-decision-summary.component').then((c) => c.OverallDecisionSummaryComponent),
      },
    ],
  },
];
