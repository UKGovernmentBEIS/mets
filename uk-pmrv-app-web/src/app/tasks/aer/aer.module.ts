import { NgModule } from '@angular/core';

import { EmissionPointsTableComponent } from '@shared/components/emission-points/emission-points-table/emission-points-table.component';
import { EmissionSourceTableComponent } from '@shared/components/emission-sources/emission-source-table/emission-source-table.component';
import { SourceStreamsTableComponent } from '@shared/components/source-streams/source-streams-table/source-streams-table.component';
import { SharedModule } from '@shared/shared.module';
import { ActivityLevelReportComponent as ReviewActivityLevelReportComponent } from '@tasks/aer/review/activity-level-report/activity-level-report.component';
import { AdditionalInfoComponent as ReviewAdditionalInfoComponent } from '@tasks/aer/review/additional-info/additional-info.component';
import { ApproachesComponent as ReviewApproachesComponent } from '@tasks/aer/review/approaches/approaches.component';
import { CompleteReviewComponent } from '@tasks/aer/review/complete-review/complete-review.component';
import { ComplianceMonitoringComponent as ReviewComplianceMonitoringComponent } from '@tasks/aer/review/compliance-monitoring/compliance-monitoring.component';
import { DataGapsComponent as ReviewDataGapsComponent } from '@tasks/aer/review/data-gaps/data-gaps.component';
import { DetailsComponent as ReviewDetailsComponent } from '@tasks/aer/review/details/details.component';
import { EmissionsSummaryComponent as ReviewEmissionsSummaryComponent } from '@tasks/aer/review/emissions-summary/emissions-summary.component';
import { FallbackComponent as ReviewFallbackComponent } from '@tasks/aer/review/fallback/fallback.component';
import { FuelsComponent as ReviewFuelsComponent } from '@tasks/aer/review/fuels/fuels.component';
import { InherentCo2Component as ReviewInherentCo2Component } from '@tasks/aer/review/inherent-co2/inherent-co2.component';
import { MaterialityLevelComponent as ReviewMaterialityLevelComponent } from '@tasks/aer/review/materiality-level/materiality-level.component';
import { MeasurementReviewComponent } from '@tasks/aer/review/measurement/measurement-review.component';
import { MisstatementsComponent as ReviewMisstatementsComponent } from '@tasks/aer/review/misstatements/misstatements.component';
import { OpinionStatementComponent as ReviewOpinionStatementComponent } from '@tasks/aer/review/opinion-statement/opinion-statement.component';
import { OverallDecisionComponent as ReviewOverallDecisionComponent } from '@tasks/aer/review/overall-decision/overall-decision.component';
import { ReviewContainerComponent } from '@tasks/aer/review/review-container.component';
import { SummaryOfConditionsComponent as ReviewSummaryOfConditionsComponent } from '@tasks/aer/review/summary-of-conditions/summary-of-conditions.component';
import { VerifiedActivityLevelReportComponent as ReviewVerifiedActivityLevelReportComponent } from '@tasks/aer/review/verified-activity-level-report/verified-activity-level-report.component';
import { VerifierDetailsComponent as ReviewVerifierDetailsComponent } from '@tasks/aer/review/verifier-details/verifier-details.component';
import { ReviewWaitComponent } from '@tasks/aer/review-wait/review-wait.component';
import { AbbreviationsComponent } from '@tasks/aer/submit/abbreviations/abbreviations.component';
import { SummaryComponent as SummaryAbbreviationsComponent } from '@tasks/aer/submit/abbreviations/summary/summary.component';
import { SummaryComponent as SummaryAdditionalDocumentsComponent } from '@tasks/aer/submit/additional-documents/summary/summary.component';
import { ApproachesComponent } from '@tasks/aer/submit/approaches/approaches.component';
import { ApproachesAddComponent } from '@tasks/aer/submit/approaches/approaches-add/approaches-add.component';
import { ApproachesDeleteComponent } from '@tasks/aer/submit/approaches/approaches-delete/approaches-delete.component';
import { ApproachesHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/approaches-help.component';
import { SourceStreamHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/source-stream-help.component';
import { ConfidentialityStatementComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement.component';
import { SummaryComponent as SummaryConfidentialityStatementComponent } from '@tasks/aer/submit/confidentiality-statement/summary/summary.component';
import { EmissionPointDeleteComponent } from '@tasks/aer/submit/emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointsComponent } from '@tasks/aer/submit/emission-points/emission-points.component';
import { EmissionSourceDeleteComponent } from '@tasks/aer/submit/emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourcesComponent } from '@tasks/aer/submit/emission-sources/emission-sources.component';
import { MonitoringPlanComponent } from '@tasks/aer/submit/monitoring-plan/monitoring-plan.component';
import { SummaryComponent as SummaryMonitoringPlanComponent } from '@tasks/aer/submit/monitoring-plan/summary/summary.component';
import { NaceCodeDeleteComponent } from '@tasks/aer/submit/nace-codes/nace-code-delete/nace-code-delete.component';
import { NaceCodesComponent } from '@tasks/aer/submit/nace-codes/nace-codes.component';
import { NaceCodeInstallationActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.component';
import { NaceCodeMainActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity.component';
import { NaceCodeSubCategoryComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.component';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { DeleteComponent as PrtrDeleteComponent } from '@tasks/aer/submit/prtr/activity/delete/delete.component';
import { PrtrComponent } from '@tasks/aer/submit/prtr/prtr.component';
import { SummaryComponent as PrtrSummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { CapacityComponent } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity.component';
import { CrfCodesComponent } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes.component';
import { EnergyCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code.component';
import { IndustrialCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code.component';
import { RegulatedActivityComponent } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.component';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/submit/regulated-activities/delete/regulated-activity-delete.component';
import { RegulatedActivitiesComponent } from '@tasks/aer/submit/regulated-activities/regulated-activities.component';
import { RegulatorComponent } from '@tasks/aer/submit/send-report/regulator/regulator.component';
import { SendReportComponent } from '@tasks/aer/submit/send-report/send-report.component';
import { VerificationComponent } from '@tasks/aer/submit/send-report/verification/verification.component';
import { SourceStreamDeleteComponent } from '@tasks/aer/submit/source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsComponent } from '@tasks/aer/submit/source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from '@tasks/aer/submit/source-streams/source-streams.component';
import { AdditionalInfoComponent } from '@tasks/aer/verification-submit/additional-info/additional-info.component';
import { CalculationEmissionsComponent } from '@tasks/aer/verification-submit/calculation-emissions/calculation-emissions.component';
import { ComplianceMonitoringComponent } from '@tasks/aer/verification-submit/compliance-monitoring/compliance-monitoring.component';
import { SummaryComponent as ComplianceMonitoringSummaryComponent } from '@tasks/aer/verification-submit/compliance-monitoring/summary/summary.component';
import { ConservativeMethodComponent } from '@tasks/aer/verification-submit/data-gaps/conservative-method/conservative-method.component';
import { DataGapsComponent } from '@tasks/aer/verification-submit/data-gaps/data-gaps.component';
import { MaterialMisstatementComponent } from '@tasks/aer/verification-submit/data-gaps/material-misstatement/material-misstatement.component';
import { RegulatorApprovedComponent } from '@tasks/aer/verification-submit/data-gaps/regulator-approved/regulator-approved.component';
import { SummaryComponent as SummaryDataGapsComponent } from '@tasks/aer/verification-submit/data-gaps/summary/summary.component';
import { DetailsComponent } from '@tasks/aer/verification-submit/details/details.component';
import { EmissionsSummaryComponent as VerifierEmissionsSummary } from '@tasks/aer/verification-submit/emissions-summary/emissions-summary.component';
import { InherentCo2Component } from '@tasks/aer/verification-submit/inherent-co2/inherent-co2.component';
import { MaterialityLevelComponent } from '@tasks/aer/verification-submit/materiality-level/materiality-level.component';
import { ReferenceDocumentsComponent } from '@tasks/aer/verification-submit/materiality-level/reference-documents/reference-documents.component';
import { SummaryComponent as MaterialityLevelSummary } from '@tasks/aer/verification-submit/materiality-level/summary/summary.component';
import { NotVerifiedComponent } from '@tasks/aer/verification-submit/overall-decision/not-verified/not-verified.component';
import { OverallDecisionComponent } from '@tasks/aer/verification-submit/overall-decision/overall-decision.component';
import { ReasonItemDeleteComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/delete/reason-item-delete.component';
import { ReasonItemComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-item.component';
import { ReasonListComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-list.component';
import { SummaryComponent as OverallDecisionSummaryComponent } from '@tasks/aer/verification-submit/overall-decision/summary/summary.component';
import { SendReportComponent as VerifierSendReportComponent } from '@tasks/aer/verification-submit/send-report/send-report.component';
import { IdentifiedChangesComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes/identified-changes.component';
import { IdentifiedChangesDeleteComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/delete/identified-changes-delete.component';
import { IdentifiedChangesItemComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-item.component';
import { IdentifiedChangesListComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-list.component';
import { NotIncludedDeleteComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/delete/not-included-delete.component';
import { NotIncludedItemComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-item.component';
import { NotIncludedListComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-list.component';
import { SummaryComponent as SummaryOfConditionsSummaryComponent } from '@tasks/aer/verification-submit/summary-of-conditions/summary/summary.component';
import { SummaryOfConditionsComponent } from '@tasks/aer/verification-submit/summary-of-conditions/summary-of-conditions.component';
import { VerificationSubmitContainerComponent } from '@tasks/aer/verification-submit/verification-submit-container.component';
import { SummaryComponent as VerifierDetailsSummaryComponent } from '@tasks/aer/verification-submit/verifier-details/summary/summary.component';
import { VerifierDetailsComponent } from '@tasks/aer/verification-submit/verifier-details/verifier-details.component';
import { VerificationWaitComponent } from '@tasks/aer/verification-wait/verification-wait.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { AerRoutingModule } from './aer-routing.module';
import { ComplianceEtsComponent } from './review/compliance-ets/compliance-ets.component';
import { NonCompliancesComponent } from './review/non-compliances/non-compliances.component';
import { NonConformitiesComponent } from './review/non-conformities/non-conformities.component';
import { RecommendedImprovementsComponent } from './review/recommended-improvements/recommended-improvements.component';
import { ReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { SkipReviewComponent } from './review/skip-review/skip-review.component';
import { AerSharedModule } from './shared/aer-shared.module';
import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { AmendComponent } from './submit/amend/amend.component';
import { AmendSummaryComponent } from './submit/amend/summary/amend-summary.component';
import { AmendSummaryTemplateComponent } from './submit/amend/summary/amend-summary-template.component';
import { EmissionsSummaryComponent } from './submit/emissions-summary/emissions-summary.component';
import { InstallationDetailsComponent } from './submit/installation-details/installation-details.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { ActivityLevelReportComponent } from './verification-submit/activity-level-report/activity-level-report.component';
import { FallbackComponent } from './verification-submit/fallback/fallback.component';
import { FuelsComponent } from './verification-submit/fuels/fuels.component';
import { MeasurementVerificationComponent } from './verification-submit/measurement/measurement-verification.component';
import { PfcVerificationComponent } from './verification-submit/pfc/pfc-verification.component';
import { RecallComponent } from './verification-wait/recall/recall.component';

@NgModule({
  declarations: [
    AbbreviationsComponent,
    ActivityComponent,
    ActivityLevelReportComponent,
    AdditionalDocumentsComponent,
    AdditionalInfoComponent,
    AmendComponent,
    AmendSummaryComponent,
    AmendSummaryTemplateComponent,
    ApproachesAddComponent,
    ApproachesComponent,
    ApproachesDeleteComponent,
    ApproachesHelpComponent,
    CalculationEmissionsComponent,
    CapacityComponent,
    CompleteReviewComponent,
    ComplianceEtsComponent,
    ComplianceMonitoringComponent,
    ComplianceMonitoringSummaryComponent,
    ConfidentialityStatementComponent,
    ConservativeMethodComponent,
    CrfCodesComponent,
    DataGapsComponent,
    DetailsComponent,
    EmissionPointDeleteComponent,
    EmissionPointDetailsComponent,
    EmissionPointsComponent,
    EmissionSourceDeleteComponent,
    EmissionSourceDetailsComponent,
    EmissionSourcesComponent,
    EmissionsSummaryComponent,
    EnergyCrfCodeComponent,
    FallbackComponent,
    FuelsComponent,
    IdentifiedChangesComponent,
    IdentifiedChangesDeleteComponent,
    IdentifiedChangesItemComponent,
    IdentifiedChangesListComponent,
    IndustrialCrfCodeComponent,
    InherentCo2Component,
    InstallationDetailsComponent,
    MaterialityLevelComponent,
    MaterialityLevelSummary,
    MaterialMisstatementComponent,
    MeasurementReviewComponent,
    MeasurementVerificationComponent,
    MonitoringPlanComponent,
    NaceCodeDeleteComponent,
    NaceCodeInstallationActivityComponent,
    NaceCodeMainActivityComponent,
    NaceCodesComponent,
    NaceCodeSubCategoryComponent,
    NonCompliancesComponent,
    NonConformitiesComponent,
    NotIncludedDeleteComponent,
    NotIncludedItemComponent,
    NotIncludedListComponent,
    NotVerifiedComponent,
    OverallDecisionComponent,
    OverallDecisionSummaryComponent,
    PfcVerificationComponent,
    PrtrComponent,
    PrtrDeleteComponent,
    PrtrSummaryComponent,
    ReasonItemComponent,
    ReasonItemDeleteComponent,
    ReasonListComponent,
    RecallComponent,
    RecommendedImprovementsComponent,
    ReferenceDocumentsComponent,
    RegulatedActivitiesComponent,
    RegulatedActivityComponent,
    RegulatedActivityDeleteComponent,
    RegulatorApprovedComponent,
    RegulatorComponent,
    ReturnForAmendsComponent,
    ReviewActivityLevelReportComponent,
    ReviewAdditionalInfoComponent,
    ReviewApproachesComponent,
    ReviewComplianceMonitoringComponent,
    ReviewContainerComponent,
    ReviewDataGapsComponent,
    ReviewDetailsComponent,
    ReviewEmissionsSummaryComponent,
    ReviewFallbackComponent,
    ReviewFuelsComponent,
    ReviewInherentCo2Component,
    ReviewMaterialityLevelComponent,
    ReviewMisstatementsComponent,
    ReviewOpinionStatementComponent,
    ReviewOverallDecisionComponent,
    ReviewSummaryOfConditionsComponent,
    ReviewVerifiedActivityLevelReportComponent,
    ReviewVerifierDetailsComponent,
    ReviewWaitComponent,
    SendReportComponent,
    SkipReviewComponent,
    SourceStreamDeleteComponent,
    SourceStreamDetailsComponent,
    SourceStreamHelpComponent,
    SourceStreamsComponent,
    SubmitContainerComponent,
    SummaryAbbreviationsComponent,
    SummaryAdditionalDocumentsComponent,
    SummaryConfidentialityStatementComponent,
    SummaryDataGapsComponent,
    SummaryMonitoringPlanComponent,
    SummaryOfConditionsComponent,
    SummaryOfConditionsSummaryComponent,
    VerificationComponent,
    VerificationSubmitContainerComponent,
    VerificationWaitComponent,
    VerifierDetailsComponent,
    VerifierDetailsSummaryComponent,
    VerifierEmissionsSummary,
    VerifierSendReportComponent,
  ],
  imports: [
    AerRoutingModule,
    AerSharedModule,
    EmissionPointsTableComponent,
    EmissionSourceTableComponent,
    SharedModule,
    SourceStreamsTableComponent,
    TaskSharedModule,
  ],
})
export class AerModule {}
