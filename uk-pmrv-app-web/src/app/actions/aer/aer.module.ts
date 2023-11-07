import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../shared/action-shared-module';
import { AerComponent } from './aer.component';
import { AerRoutingModule } from './aer-routing.module';
import { CompletedComponent } from './completed/completed.component';
import { ReturnForAmendsComponent } from './return-for-amends/return-for-amends.component';
import { ActivityLevelReportComponent as ReviewedActivityLevelReportComponent } from './reviewed/activity-level-report/activity-level-report.component';
import { AdditionalInfoComponent as ReviewedAdditionalInfoComponent } from './reviewed/additional-info/additional-info.component';
import { ApproachesComponent as ReviewedApproachesComponent } from './reviewed/approaches/approaches.component';
import { ComplianceEtsComponent as ReviewedComplianceEtsComponent } from './reviewed/compliance-ets/compliance-ets.component';
import { ComplianceMonitoringComponent as ReviewedComplianceMonitoringComponent } from './reviewed/compliance-monitoring/compliance-monitoring.component';
import { DataGapsComponent as ReviewedDataGapsComponent } from './reviewed/data-gaps/data-gaps.component';
import { DetailsComponent as ReviewedDetailsComponent } from './reviewed/details/details.component';
import { EmissionsSummaryComponent as ReviewedEmissionsSummaryComponent } from './reviewed/emissions-summary/emissions-summary.component';
import { FallbackComponent as ReviewedFallbackComponent } from './reviewed/fallback/fallback.component';
import { FuelsComponent as ReviewedFuelsComponent } from './reviewed/fuels/fuels.component';
import { InherentCo2Component as ReviewedInherentCo2Component } from './reviewed/inherent-co2/inherent-co2.component';
import { MaterialityLevelComponent as ReviewedMaterialityLevelComponent } from './reviewed/materiality-level/materiality-level.component';
import { MeasurementComponent as ReviewedMeasurementComponent } from './reviewed/measurement/measurement.component';
import { MisstatementsComponent as ReviewedMisstatementsComponent } from './reviewed/misstatements/misstatements.component';
import { NonCompliancesComponent as ReviewedNonCompliancesComponent } from './reviewed/non-compliances/non-compliances.component';
import { NonConformitiesComponent as ReviewedNonConformitiesComponent } from './reviewed/non-conformities/non-conformities.component';
import { OpinionStatementComponent as ReviewedOpinionStatementComponent } from './reviewed/opinion-statement/opinion-statement.component';
import { OverallDecisionComponent as ReviewedOverallDecisionComponent } from './reviewed/overall-decision/overall-decision.component';
import { RecommendedImprovementsComponent as ReviewedRecommendedImprovementsComponent } from './reviewed/recommended-improvements/recommended-improvements.component';
import { ReviewedComponent } from './reviewed/reviewed.component';
import { SummaryOfConditionsComponent as ReviewedSummaryOfConditionsComponent } from './reviewed/summary-of-conditions/summary-of-conditions.component';
import { VerifiedActivityLevelReportComponent as ReviewedVerifiedActivityLevelReportComponent } from './reviewed/verified-activity-level-report/verified-activity-level-report.component';
import { VerifierDetailsComponent as ReviewedVerifierDetailsComponent } from './reviewed/verifier-details/verifier-details.component';
import { AbbreviationsComponent } from './submitted/abbreviations/abbreviations.component';
import { ActivityLevelReportComponent } from './submitted/activity-level-report/activity-level-report.component';
import { AdditionalDocumentsComponent } from './submitted/additional-documents/additional-documents.component';
import { AdditionalInfoComponent } from './submitted/additional-info/additional-info.component';
import { ApproachesComponent } from './submitted/approaches-tier/approaches.component';
import { ApproachesTierComponent } from './submitted/approaches-tier/approaches-tier.component';
import { ComplianceEtsComponent } from './submitted/compliance-ets/compliance-ets.component';
import { ComplianceMonitoringComponent } from './submitted/compliance-monitoring/compliance-monitoring.component';
import { ConfidentialityStatementComponent } from './submitted/confidentiality-statement/confidentiality-statement.component';
import { DataGapsComponent } from './submitted/data-gaps/data-gaps.component';
import { DetailsComponent } from './submitted/details/details.component';
import { EmissionPointsComponent } from './submitted/emission-points/emission-points.component';
import { EmissionSourcesComponent } from './submitted/emission-sources/emission-sources.component';
import { EmissionsSummaryComponent } from './submitted/emissions-summary/emissions-summary.component';
import { FallbackComponent } from './submitted/fallback/fallback.component';
import { FuelsComponent } from './submitted/fuels/fuels.component';
import { InherentCo2Component } from './submitted/inherent-co2/inherent-co2.component';
import { InstallationDetailsComponent } from './submitted/installation-details/installation-details.component';
import { MaterialityLevelComponent } from './submitted/materiality-level/materiality-level.component';
import { MeasurementComponent } from './submitted/measurement/measurement.component';
import { MeasurementTierComponent } from './submitted/measurement/measurement-tier.component';
import { MisstatementsComponent } from './submitted/misstatements/misstatements.component';
import { MonitoringApproachesComponent } from './submitted/monitoring-approaches/monitoring-approaches.component';
import { MonitoringPlanComponent } from './submitted/monitoring-plan/monitoring-plan.component';
import { NaceCodesComponent } from './submitted/nace-codes/nace-codes.component';
import { NonCompliancesComponent } from './submitted/non-compliances/non-compliances.component';
import { NonConformitiesComponent } from './submitted/non-conformities/non-conformities.component';
import { OpinionStatementComponent } from './submitted/opinion-statement/opinion-statement.component';
import { OverallDecisionComponent } from './submitted/overall-decision/overall-decision.component';
import { PrtrComponent } from './submitted/prtr/prtr.component';
import { RecommendedImprovementsComponent } from './submitted/recommended-improvements/recommended-improvements.component';
import { RegulatedActivitiesComponent } from './submitted/regulated-activities/regulated-activities.component';
import { SourceStreamsComponent } from './submitted/source-streams/source-streams.component';
import { SubmittedComponent } from './submitted/submitted.component';
import { SummaryOfConditionsComponent } from './submitted/summary-of-conditions/summary-of-conditions.component';
import { VerifiedActivityLevelReportComponent } from './submitted/verified-activity-level-report/verified-activity-level-report.component';
import { VerifierDetailsComponent } from './submitted/verifier-details/verifier-details.component';

@NgModule({
  declarations: [
    AbbreviationsComponent,
    ActivityLevelReportComponent,
    AdditionalDocumentsComponent,
    AdditionalInfoComponent,
    AerComponent,
    ApproachesComponent,
    ApproachesTierComponent,
    CompletedComponent,
    ComplianceEtsComponent,
    ComplianceMonitoringComponent,
    ConfidentialityStatementComponent,
    DataGapsComponent,
    DetailsComponent,
    EmissionPointsComponent,
    EmissionSourcesComponent,
    EmissionsSummaryComponent,
    FallbackComponent,
    FuelsComponent,
    InherentCo2Component,
    InstallationDetailsComponent,
    MaterialityLevelComponent,
    MeasurementComponent,
    MeasurementTierComponent,
    MisstatementsComponent,
    MonitoringApproachesComponent,
    MonitoringPlanComponent,
    NaceCodesComponent,
    NonCompliancesComponent,
    NonConformitiesComponent,
    OpinionStatementComponent,
    OverallDecisionComponent,
    PrtrComponent,
    RecommendedImprovementsComponent,
    RegulatedActivitiesComponent,
    ReturnForAmendsComponent,
    ReviewedActivityLevelReportComponent,
    ReviewedAdditionalInfoComponent,
    ReviewedApproachesComponent,
    ReviewedComplianceEtsComponent,
    ReviewedComplianceMonitoringComponent,
    ReviewedComponent,
    ReviewedDataGapsComponent,
    ReviewedDetailsComponent,
    ReviewedEmissionsSummaryComponent,
    ReviewedFallbackComponent,
    ReviewedFuelsComponent,
    ReviewedInherentCo2Component,
    ReviewedMaterialityLevelComponent,
    ReviewedMeasurementComponent,
    ReviewedMisstatementsComponent,
    ReviewedNonCompliancesComponent,
    ReviewedNonConformitiesComponent,
    ReviewedOpinionStatementComponent,
    ReviewedOverallDecisionComponent,
    ReviewedRecommendedImprovementsComponent,
    ReviewedSummaryOfConditionsComponent,
    ReviewedVerifiedActivityLevelReportComponent,
    ReviewedVerifierDetailsComponent,
    SourceStreamsComponent,
    SubmittedComponent,
    SummaryOfConditionsComponent,
    VerifiedActivityLevelReportComponent,
    VerifierDetailsComponent,
  ],
  imports: [ActionSharedModule, AerRoutingModule, SharedModule],
})
export class AerModule {}
