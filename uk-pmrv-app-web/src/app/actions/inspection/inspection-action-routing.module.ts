import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { InspectionActionComponent } from './inspection-action.component';
import { OnsiteAuditSubmittedComponent } from './onsite-audit-submitted/onsite-audit-submitted.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';

const routes: Routes = [
  {
    path: '',
    component: InspectionActionComponent,
    children: [
      {
        path: 'peer-review-decision',
        component: PeerReviewDecisionComponent,
        data: { pageTitle: 'Peer review decision' },
      },
      {
        path: 'onsite-audit-submitted',
        component: OnsiteAuditSubmittedComponent,
        data: { pageTitle: 'On-site inspection' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InspectionActionRoutingModule {}
