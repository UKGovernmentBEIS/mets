import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { LegalEntityDetailsComponent } from '../shared/legal-entity-details/legal-entity-details.component';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { ApplicationTypeComponent } from './application-type/application-type.component';
import { CancelApplicationComponent } from './cancel-application/cancel-application.component';
import { CommencementComponent } from './commencement/commencement.component';
import { ConfirmResponsibilityComponent } from './confirm-responsibility/confirm-responsibility.component';
import { EtsSchemeComponent } from './ets-scheme/ets-scheme.component';
import { installationFormFactory } from './factories/installation-form.factory';
import { legalEntityFormOpFactory } from './factories/legal-entity/legal-entity-form-op.factory';
import { legalEntityFormRegFactory } from './factories/legal-entity/legal-entity-form-reg.factory';
import { GasEmissionsDetailsComponent } from './gas-emissions-details/gas-emissions-details.component';
import { GasEmissionsDetailsGuard } from './gas-emissions-details/gas-emissions-details.guard';
import { ApplicationGuard } from './guards/application.guard';
import { FormGuard } from './guards/form.guard';
import { LegalEntityDetailsGuard } from './guards/legal-entity-details.guard';
import { InstallationAccountApplicationRoutingModule } from './installation-account-application-routing.module';
import { InstallationTypeComponent } from './installation-type/installation-type.component';
import { LegalEntityDetailsOpComponent } from './legal-entity-operator/legal-entity-details-op/legal-entity-details-op.component';
import { LegalEntityDetailsOpGuard } from './legal-entity-operator/legal-entity-details-op/legal-entity-details-op.guard';
import { LegalEntityRegnoOpComponent } from './legal-entity-operator/legal-entity-regno-op/legal-entity-regno-op.component';
import { LegalEntityRegnoOpGuard } from './legal-entity-operator/legal-entity-regno-op/legal-entity-regno-op.guard';
import { LegalEntitySelectOpComponent } from './legal-entity-operator/legal-entity-select-op/legal-entity-select-op.component';
import { LegalEntitySelectOpGuard } from './legal-entity-operator/legal-entity-select-op/legal-entity-select-op.guard';
import { LegalEntitySelectRegComponent } from './legal-entity-regulator/legal-entity-select-reg/legal-entity-select-reg.component';
import { LegalEntitySelectRegGuard } from './legal-entity-regulator/legal-entity-select-reg/legal-entity-select-reg.guard';
import { OffshoreGuard } from './offshore-details/offshore.guard';
import { OffshoreDetailsComponent } from './offshore-details/offshore-details.component';
import { OnshoreGuard } from './onshore-details/onshore.guard';
import { OnshoreDetailsComponent } from './onshore-details/onshore-details.component';
import { OperatorApplicationComponent } from './operator-application/operator-application.component';
import { ReviewComponent } from './review/review.component';
import { ReviewSummaryComponent } from './review/review-summary.component';
import { SubmittedDecisionComponent } from './submitted-decision/submitted-decision.component';
import { SummaryComponent } from './summary/summary.component';
import { TaskListComponent } from './task-list/task-list.component';

@NgModule({
  declarations: [
    ApplicationSubmittedComponent,
    ApplicationTypeComponent,
    CancelApplicationComponent,
    CommencementComponent,
    ConfirmResponsibilityComponent,
    EtsSchemeComponent,
    GasEmissionsDetailsComponent,
    InstallationTypeComponent,
    LegalEntityDetailsComponent,
    LegalEntityDetailsOpComponent,
    LegalEntityRegnoOpComponent,
    LegalEntitySelectOpComponent,
    LegalEntitySelectRegComponent,
    OffshoreDetailsComponent,
    OnshoreDetailsComponent,
    OperatorApplicationComponent,
    ReviewComponent,
    ReviewSummaryComponent,
    SubmittedDecisionComponent,
    SummaryComponent,
    TaskListComponent,
  ],
  imports: [InstallationAccountApplicationRoutingModule, SharedModule],
  providers: [
    ApplicationGuard,
    FormGuard,
    GasEmissionsDetailsGuard,
    installationFormFactory,
    LegalEntityDetailsGuard,
    LegalEntityDetailsOpGuard,
    legalEntityFormOpFactory,
    legalEntityFormRegFactory,
    LegalEntityRegnoOpGuard,
    LegalEntitySelectOpGuard,
    LegalEntitySelectRegGuard,
    OffshoreGuard,
    OnshoreGuard,
  ],
})
export class InstallationAccountApplicationModule {}
