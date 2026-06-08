import { Routes } from '@angular/router';

import { routerInputDataResolver } from '@actions/request-action.util';
import { VerificationReturnedToOperatorComponent } from '@shared/components/verification-returned-to-operator/verification-returned-to-operator.component';

import {
  BaselineSubmittedComponent,
  BdrOpinionStatementSubmittedComponent,
  BdrOverallDecisionSubmittedComponent,
  BdrPeerReviewDecisionComponent,
  BdrReturnedForAmendsComponent,
  BdrSubmittedComponent,
  OutcomeCompletedComponent,
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
    resolve: {
      input: routerInputDataResolver,
    },
    data: { pageTitle: 'Baseline data report returned to operator from verifier' },
    component: VerificationReturnedToOperatorComponent,
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'Baseline data report returned for amends' },
    component: BdrReturnedForAmendsComponent,
  },
  {
    path: 'peer-review-decision',
    component: BdrPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
];
