import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { DreSummaryComponent } from './submitted/dre-summary/dre-summary.component';
import { SubmittedComponent } from './submitted/submitted.component';

const routes: Route[] = [
  {
    path: 'submitted',
    data: { pageTitle: 'Reportable emissions' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Reportable emissions' },
        component: SubmittedComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Reportable emissions', breadcrumb: 'Summary' },
        component: DreSummaryComponent,
      },
    ],
  },
  {
    path: 'peer-review-decision',
    component: PeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DreRoutingModule {}
