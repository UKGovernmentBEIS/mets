import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../shared/action-shared-module';
import { InspectionActionComponent } from './inspection-action.component';
import { InspectionActionRoutingModule } from './inspection-action-routing.module';
import { OnsiteAuditSubmittedComponent } from './onsite-audit-submitted/onsite-audit-submitted.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';

@NgModule({
  declarations: [InspectionActionComponent],
  imports: [
    ActionSharedModule,
    InspectionActionRoutingModule,
    OnsiteAuditSubmittedComponent,
    PeerReviewDecisionComponent,
    SharedModule,
  ],
})
export class InspectionActionModule {}
