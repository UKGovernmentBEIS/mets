import { NgModule } from '@angular/core';

import { NonComplianceModule } from '@actions/non-compliance/non-compliance.module';
import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { RequestActionPageComponent } from '@aviation/request-action/containers';
import { RespondedComponent } from '@aviation/request-action/vir/responded/responded.component';
import { VirActionTaskListComponent } from '@aviation/shared/components/vir/vir-action-task-list/vir-action-task-list.component';
import { SharedModule } from '@shared/shared.module';

import { ThreeYearPeriodOffsettingRequirementsSubmittedComponent } from './3year-period-offsetting-requirements-submitted/3year-period-offsetting-requirements-submitted.component';
import { AccountClosedSubmittedComponent } from './account-closed-submitted';
import { AerReturnForAmendsComponent } from './aer/shared/return-for-amends/aer-return-for-amends.component';
import { AviationAerVerifierReturnedComponent } from './aer/shared/verifier-returned/verifier-returned.component';
import { AnnualOffsettingRequirementsSubmittedComponent } from './annual-offsetting-requirements-submitted/annual-offsetting-requirements-submitted.component';
import { DoeEmissionsUpdatedComponent } from './doe/doe-emissions-updated/doe-emissions-updated.component';
import { AviationEmissionsUpdatedComponent } from './dre/aviation-emissions-updated/aviation-emissions-updated.component';
import { EmpReturnForAmendsComponent } from './emp/return-for-amends/return-for-amends.component';
import { CompletedComponent as BatchReissueCompletedComponent } from './emp-batch-reissue/batch-reissue/completed/completed.component';
import { SubmittedComponent } from './emp-batch-reissue/batch-reissue/submitted/submitted.component';
import { CompletedComponent as ReissueCompletedComponent } from './emp-batch-reissue/reissue/completed/completed.component';
import { RequestActionRoutingModule } from './request-action-routing.module';

@NgModule({
  declarations: [RequestActionPageComponent],
  imports: [
    AccountClosedSubmittedComponent,
    ActionSharedModule,
    AerReturnForAmendsComponent,
    AnnualOffsettingRequirementsSubmittedComponent,
    AviationAerVerifierReturnedComponent,
    AviationEmissionsUpdatedComponent,
    BatchReissueCompletedComponent,
    DoeEmissionsUpdatedComponent,
    EmpReturnForAmendsComponent,
    NonComplianceModule,
    ReissueCompletedComponent,
    RequestActionRoutingModule,
    RespondedComponent,
    SharedModule,
    SubmittedComponent,
    ThreeYearPeriodOffsettingRequirementsSubmittedComponent,
    VirActionTaskListComponent,
  ],
})
export class RequestActionModule {}
