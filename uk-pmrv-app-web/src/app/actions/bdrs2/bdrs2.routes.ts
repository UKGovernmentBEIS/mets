import { Routes } from '@angular/router';

import {
  Bdrs2BaselineSubmittedComponent,
  Bdrs2OpinionStatementSubmittedComponent,
  Bdrs2OverallDecisionSubmittedComponent,
  Bdrs2PeerReviewDecisionComponent,
  Bdrs2ReturnedForAmendsComponent,
  BdrS2ReturnedToOperatorComponent,
  Bdrs2SubmittedComponent,
  OutcomeCompletedComponent,
} from '.';

export const BDRS2_ACTION_ROUTES: Routes = [
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
        data: { pageTitle: 'Stage 2 baseline data report submitted' },
        component: Bdrs2SubmittedComponent,
      },
      {
        path: 'baseline',
        data: { pageTitle: 'Stage 2 baseline data report', breadcrumb: true },
        component: Bdrs2BaselineSubmittedComponent,
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'Stage 2 BDR verification opinion statement', breadcrumb: true },
        component: Bdrs2OpinionStatementSubmittedComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'Stage 2 BDR verification overall decision', breadcrumb: true },
        component: Bdrs2OverallDecisionSubmittedComponent,
      },
      {
        path: 'outcome',
        data: { pageTitle: 'Outcome of regulator review', breadcrumb: true },
        component: OutcomeCompletedComponent,
      },
    ],
  },
  {
    path: 'returned-to-operator',
    data: { pageTitle: 'Stage 2 baseline data report returned to operator from verifier' },
    component: BdrS2ReturnedToOperatorComponent,
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'Stage 2 baseline data report returned for amendments' },
    component: Bdrs2ReturnedForAmendsComponent,
  },
  {
    path: 'peer-review-decision',
    component: Bdrs2PeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
];
