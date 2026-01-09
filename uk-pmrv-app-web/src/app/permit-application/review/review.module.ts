import { NgModule } from '@angular/core';

import { MeasurementDevicesTableComponent } from '@permit-application/shared/measurement-devices-table/measurement-devices-table.component';

import { SharedPermitVariationModule } from '../../permit-variation/shared/shared-permit-variation.module';
import { PeerReviewDecisionGuard } from '../../shared/components/peer-review-decision/peer-review-decision.guard';
import { PaymentCompletedGuard } from '../../shared/guards/payment-completed.guard';
import { SharedModule } from '../../shared/shared.module';
import { MmpEnergyFlowsSummaryTemplateComponent } from '../mmp-energy-flows/mmp-energy-flows-summary/mmp-energy-flows-summary-template/mmp-energy-flows-summary-template.component';
import { MethodsSummaryTemplateComponent } from '../mmp-methods/methods-summary/methods-summary-template/methods-summary-template.component';
import { MmpProceduresSummaryTemplateComponent } from '../mmp-procedures/mmp-procedures-summary/mmp-procedures-summary-template/mmp-procedures-summary-template.component';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { AdditionalInfoComponent } from './additional-info/additional-info.component';
import { ConfirmationComponent as ReturnForAmendsConfirmationComponent } from './amend/return-for-amends/confirmation/confirmation.component';
import { ReturnForAmendsComponent } from './amend/return-for-amends/return-for-amends.component';
import { CalculationComponent } from './calculation/calculation.component';
import { ConfidentialityComponent } from './confidentiality/confidentiality.component';
import { DecisionContainerComponent } from './decision/decision-container/decision-container.component';
import { DetailsComponent } from './details/details.component';
import { ActivationDateComponent as DeterminationActivationDateComponent } from './determination/activation-date/activation-date.component';
import { AnswersComponent as DeterminationAnswersComponent } from './determination/answers/answers.component';
import { DeterminationComponent } from './determination/determination.component';
import { EmissionsComponent as DeterminationAnnualEmissionsComponent } from './determination/emissions/emissions.component';
import { OfficialNoticeComponent as DeterminationOfficialNoticeComponent } from './determination/official-notice/official-notice.component';
import { ReasonComponent as DeterminationReasonComponent } from './determination/reason/reason.component';
import { SummaryComponent as DeterminationSummaryComponent } from './determination/summary/summary.component';
import { FallbackComponent } from './fallback/fallback.component';
import { FuelsComponent } from './fuels/fuels.component';
import { InherentCO2Component } from './inherent-co2/inherent-co2.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { MeasurementComponent } from './measurement/measurement.component';
import { MonitoringApproachesComponent } from './monitoring-approaches/monitoring-approaches.component';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { NitrousOxideComponent } from './nitrous-oxide/nitrous-oxide.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PfcComponent } from './pfc/pfc.component';
import { ConfirmationComponent as RecallConfirmationComponent } from './recall/confirmation/confirmation.component';
import { RecallComponent } from './recall/recall.component';
import { ReviewRoutingModule } from './review-routing.module';
import { TransferredCO2Component } from './transferred-co2/transferred-co2.component';
import { UncertaintyAnalysisComponent } from './uncertainty-analysis/uncertainty-analysis.component';

@NgModule({
  declarations: [
    AdditionalInfoComponent,
    CalculationComponent,
    ConfidentialityComponent,
    DecisionContainerComponent,
    DetailsComponent,
    DeterminationActivationDateComponent,
    DeterminationAnnualEmissionsComponent,
    DeterminationAnswersComponent,
    DeterminationComponent,
    DeterminationOfficialNoticeComponent,
    DeterminationReasonComponent,
    DeterminationSummaryComponent,
    FallbackComponent,
    FuelsComponent,
    InherentCO2Component,
    ManagementProceduresComponent,
    MeasurementComponent,
    MonitoringApproachesComponent,
    MonitoringMethodologyPlanComponent,
    NitrousOxideComponent,
    NotifyOperatorComponent,
    PermitTypeComponent,
    PfcComponent,
    RecallComponent,
    RecallConfirmationComponent,
    ReturnForAmendsComponent,
    ReturnForAmendsConfirmationComponent,
    TransferredCO2Component,
    UncertaintyAnalysisComponent,
  ],
  imports: [
    MeasurementDevicesTableComponent,
    MethodsSummaryTemplateComponent,
    MmpEnergyFlowsSummaryTemplateComponent,
    MmpProceduresSummaryTemplateComponent,
    ReviewRoutingModule,
    SharedModule,
    SharedPermitModule,
    SharedPermitVariationModule,
  ],
  providers: [PaymentCompletedGuard, PeerReviewDecisionGuard],
})
export class ReviewModule {}
