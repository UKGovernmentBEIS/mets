import { Routes } from '@angular/router';

import { HsetiDetailsSubmittedComponent, HseTiSubmittedComponent } from '.';
import { HsetiPeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { HsetiReturnedForAmendsComponent } from './returned-for-amends/returned-for-amends.component';
import { HsetiOverallDecisionSubmittedComponent } from './submitted/overall-decision/overall-decision.component';

export const HSETI_ACTION_ROUTES: Routes = [
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
        data: { pageTitle: 'HSE target increase details submitted' },
        component: HseTiSubmittedComponent,
      },
      {
        path: 'details',
        data: { pageTitle: 'HSE target increase details', breadcrumb: true },
        component: HsetiDetailsSubmittedComponent,
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'Overall decision', breadcrumb: true },
        component: HsetiOverallDecisionSubmittedComponent,
      },
    ],
  },
  {
    path: 'return-for-amends',
    data: { pageTitle: 'HSE target increase application returned for amends' },
    component: HsetiReturnedForAmendsComponent,
  },
  {
    path: 'peer-review-decision',
    component: HsetiPeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
];
