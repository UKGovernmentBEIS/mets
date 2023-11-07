import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { ReturnedComponent } from './returned/returned.component';
import { SubmittedComponent } from './submitted/submitted.component';

const routes: Route[] = [
  {
    path: 'submitted',
    data: { pageTitle: 'Return of allowances submitted' },
    component: SubmittedComponent,
  },
  {
    path: 'roa-peer-review-decision',
    component: PeerReviewDecisionComponent,
    data: { pageTitle: 'Peer review decision' },
  },
  {
    path: 'returned',
    component: ReturnedComponent,
    data: { pageTitle: 'Returned allowances' },
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReturnOfAllowancesRoutingModule {}
