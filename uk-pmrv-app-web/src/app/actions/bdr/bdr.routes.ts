import { Routes } from '@angular/router';

import {
  BaselineSubmittedComponent,
  BdrOpinionStatementSubmittedComponent,
  BdrOverallDecisionSubmittedComponent,
  BdrPeerReviewDecisionComponent,
  BdrReturnedToOperatorComponent,
  BdrSubmittedComponent,
  OutcomeCompletedComponent,
  ReturnedForAmendsComponent,
} from '.';

export const BDR_ACTION_ROUTES: Routes = [
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
        data: { pageTitle: 'Baseline data report submitted' },
        component: BdrSubmittedComponent,
      },
      {
        path: 'baseline',
        data: { pageTitle: 'Baseline data report and details', breadcrumb: true },
        component: BaselineSubmittedComponent,
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'BDR verification opinion statement', breadcrumb: true },
        component: BdrOpinionStatementSubmittedComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'BDR verification overall decision', breadcrumb: true },
        component: BdrOverallDecisionSubmittedComponent,
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
    data: { pageTitle: 'Baseline data report returned to operator from verifier' },
    component: BdrReturnedToOperatorComponent,
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'Baseline data report returned for amends' },
    component: ReturnedForAmendsComponent,
  },
  {
    path: 'peer-review-decision',
    component: BdrPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
];
