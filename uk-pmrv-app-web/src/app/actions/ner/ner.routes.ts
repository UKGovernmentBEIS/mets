import { Routes } from '@angular/router';

import { routerInputDataResolver } from '@actions/request-action.util';

export const NER_ACTION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
  {
    path: 'submitted',
    children: [
      {
        path: '',
        data: { pageTitle: 'New entrant reserve submitted to verifier' },
        loadComponent: () => import('./submitted').then((c) => c.NerSubmittedComponent),
      },
      {
        path: 'details',
        data: { pageTitle: 'New entrant reserve', breadcrumb: true },
        loadComponent: () => import('./submitted').then((c) => c.NerActionDetailsComponent),
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'NER verification opinion statement', breadcrumb: true },
        loadComponent: () => import('./submitted').then((c) => c.NerOpinionStatementSubmittedComponent),
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'NER verification overall decision', breadcrumb: true },
        loadComponent: () => import('./submitted').then((c) => c.NerOverallDecisionSubmittedComponent),
      },
      {
        path: 'outcome',
        data: { pageTitle: 'Outcome of regulator review', breadcrumb: true },
        loadComponent: () => import('./submitted').then((c) => c.NerActionOutcomeComponent),
      },
    ],
  },
  {
    path: 'returned-to-operator',
    resolve: {
      input: routerInputDataResolver,
    },
    data: { pageTitle: 'New entrant reserve returned to operator from verifier' },
    loadComponent: () =>
      import('../../shared/components/verification-returned-to-operator/verification-returned-to-operator.component').then(
        (c) => c.VerificationReturnedToOperatorComponent,
      ),
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'New entrance reserve returned for amendments' },
    loadComponent: () =>
      import('./returned-for-amends/returned-for-amends.component').then((c) => c.NerReturnedForAmendsComponent),
  },
  {
    path: 'peer-review-decision',
    loadComponent: () =>
      import('./peer-review-decision/peer-review-decision.component').then((c) => c.NerPeerReviewDecisionComponent),
    data: { pageTitle: 'Peer review decision' },
  },
];
