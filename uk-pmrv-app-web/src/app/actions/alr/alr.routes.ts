import { Routes } from '@angular/router';

import { RecallSharedComponent } from '@shared/components/recall/recall.component';

import { AlrReturnedToOperatorComponent } from './returned-to-operator/returned-to-operator.component';
import {
  AlrActivitySubmittedComponent,
  AlrOpinionStatementSubmittedComponent,
  AlrOverallDecisionSubmittedComponent,
  AlrSubmittedComponent,
} from './submitted';

export const ALR_ACTION_ROUTES: Routes = [
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
        data: { pageTitle: 'Activity level report submitted to verifier' },
        component: AlrSubmittedComponent,
      },
      {
        path: 'activity',
        data: { pageTitle: 'Provide the activity level report', breadcrumb: true },
        component: AlrActivitySubmittedComponent,
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'ALR verification opinion statement', breadcrumb: true },
        component: AlrOpinionStatementSubmittedComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'ALR verification overall decision', breadcrumb: true },
        component: AlrOverallDecisionSubmittedComponent,
      },
    ],
  },
  {
    path: 'returned-to-operator',
    data: { pageTitle: 'Activity level report returned to operator from verifier' },
    component: AlrReturnedToOperatorComponent,
  },
  {
    path: 'recall-from-verifier',
    data: { pageTitle: 'Recall the report', breadcrumb: true },
    component: RecallSharedComponent,
  },
];
