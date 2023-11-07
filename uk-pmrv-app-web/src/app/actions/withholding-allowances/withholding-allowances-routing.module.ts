import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { ClosedComponent } from './closed/closed.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { SubmittedWithdrawnComponent } from './submitted-withdrawn/submitted-withdrawn.component';

const routes: Route[] = [
  {
    path: 'submitted',
    data: {
      pageTitle: 'Withholding of allowances details',
      actionType: 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED',
    },
    component: SubmittedWithdrawnComponent,
  },
  {
    path: 'withdrawn',
    data: {
      pageTitle: 'Withdraw withholding of allowances notice',
      actionType: 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN',
    },
    component: SubmittedWithdrawnComponent,
  },
  {
    path: 'closed',
    data: {
      pageTitle: 'Details of closed task',
    },
    component: ClosedComponent,
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
export class WithholdingAllowancesRoutingModule {}
