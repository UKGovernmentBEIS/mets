import { Routes } from '@angular/router';

import { routerInputDataResolver } from '@actions/request-action.util';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { RecallSharedComponent } from '@shared/components/recall/recall.component';
import { VerificationReturnedToOperatorComponent } from '@shared/components/verification-returned-to-operator/verification-returned-to-operator.component';

import {
  AlrActionCompletedComponent,
  AlrActionCompletedSubmittedComponent,
  AlrActionDateSubmittedComponent,
  AlrActionResponseComponent,
  AlrActivitySubmittedComponent,
  AlrAlcInformationSubmittedComponent,
  AlrDeterminationSubmittedComponent,
  AlrOpinionStatementSubmittedComponent,
  AlrOverallDecisionSubmittedComponent,
  AlrPeerReviewDecisionComponent,
  AlrReturnedForAmendsComponent,
  AlrSubmittedComponent,
} from '.';
import { AlrMarkedAsNotRequiredDetailsComponent } from './marked-as-not-required/marked-as-not-required.component';

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
      {
        path: 'alc-information',
        data: { pageTitle: 'Information about this activity level change', breadcrumb: true },
        component: AlrAlcInformationSubmittedComponent,
      },
      {
        path: 'determination',
        data: { pageTitle: 'Determination of activity level', breadcrumb: true },
        component: AlrDeterminationSubmittedComponent,
      },
    ],
  },
  {
    path: 'returned-to-operator',
    resolve: {
      input: routerInputDataResolver,
    },
    data: { pageTitle: 'Activity level report returned to operator from verifier' },
    component: VerificationReturnedToOperatorComponent,
  },
  {
    path: 'recall-from-verifier',
    data: { pageTitle: 'Recall the report', breadcrumb: true },
    component: RecallSharedComponent,
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'Baseline data report returned for amends' },
    component: AlrReturnedForAmendsComponent,
  },
  {
    path: 'peer-review-decision',
    component: AlrPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'completed',
    data: { pageTitle: 'Activity level determination' },
    children: [
      {
        path: '',
        component: AlrActionCompletedComponent,
      },
      {
        path: 'submitted',
        data: { pageTitle: 'Activity level determination', backlink: '../' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Activity level determination' },
            component: AlrActionCompletedSubmittedComponent,
          },
          {
            path: 'date-submitted',
            data: {
              pageTitle: 'Provide the date application was submitted to UK authorities',
              breadcrumb: 'Provide the date application was submitted to UK authorities',
            },
            component: AlrActionDateSubmittedComponent,
          },
          {
            path: 'response',
            data: {
              pageTitle: 'Provide UK ETS Authority response',
              breadcrumb: 'Provide UK ETS Authority response',
            },
            component: AlrActionResponseComponent,
          },
        ],
      },
    ],
  },
  {
    path: 'not-required',
    data: {
      pageTitle: 'Marked as not required',
      breadcrumb: 'Marked as not required',
    },
    component: AlrMarkedAsNotRequiredDetailsComponent,
    canDeactivate: [PendingRequestGuard],
  },
];
