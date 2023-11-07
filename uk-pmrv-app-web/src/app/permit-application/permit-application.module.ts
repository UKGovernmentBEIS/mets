import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { AbbreviationsComponent } from './abbreviations/abbreviations.component';
import { AbbreviationsSummaryComponent } from './abbreviations/abbreviations-summary/abbreviations-summary.component';
import { AdditionalDocumentsComponent } from './additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryComponent } from './additional-documents/additional-documents-summary/additional-documents-summary.component';
import { AmendComponent } from './amend/amend.component';
import { AmendGuard } from './amend/amend.guard';
import { AmendSummaryComponent } from './amend/summary/amend-summary.component';
import { AmendSummaryGuard } from './amend/summary/amend-summary.guard';
import { AmendSummaryTemplateComponent } from './amend/summary/amend-summary-template.component';
import { ApproachesComponent } from './approaches/approaches.component';
import { ApproachesAddComponent } from './approaches/approaches-add/approaches-add.component';
import { ApproachesHelpComponent } from './approaches/approaches-help/approaches-help.component';
import { ApproachesPrepareComponent } from './approaches/approaches-prepare/approaches-prepare.component';
import { ApproachesPrepareSummaryComponent } from './approaches/approaches-prepare/approaches-prepare-summary/approaches-prepare-summary.component';
import { ApproachesPrepareTemplateComponent } from './approaches/approaches-prepare/approaches-prepare-template.component';
import { ApproachesSummaryComponent } from './approaches/approaches-summary/approaches-summary.component';
import { ActivityDataGuard as CalculationActivityDataGuard } from './approaches/calculation/category-tier/activity-data/activity-data.guard';
import { AnswersGuard as CalculationCategoryTierActivityDataAnswersGuard } from './approaches/calculation/category-tier/activity-data/answers/answers.guard';
import { JustificationGuard as CalculationActivityDataJustificationGuard } from './approaches/calculation/category-tier/activity-data/justification/justification.guard';
import { CategorySummaryGuard as CalculationCategorySummaryGuard } from './approaches/calculation/category-tier/category/summary/category-summary.guard';
import { TransferredCO2DetailsGuard as CalculationCategoryTransferredCO2DetailsGuard } from './approaches/calculation/category-tier/category/transferred-co2-details/transferred-co2-details.guard';
import { CategoryTierGuard as CalculationCategoryTierGuard } from './approaches/calculation/category-tier/category-tier.guard';
import { DeleteGuard as CalculationCategoryTierDeleteGuard } from './approaches/calculation/category-tier/delete/delete.guard';
import { AnswersGuard as CalculationCategoryTierAnswersGuard } from './approaches/calculation/category-tier/guards/answers.guard';
import { WizardStepGuard as CalculationCategoryTierWizardStepGuard } from './approaches/calculation/category-tier/guards/wizard-step.guard';
import { AnswersGuard as CalculationSamplingPlanAnswersGuard } from './approaches/calculation/sampling-plan/answers/answers.guard';
import { AppropriatenessGuard as CalculationSamplingPlanAppropriatenessGuard } from './approaches/calculation/sampling-plan/appropriateness/appropriateness.guard';
import { PlanGuard as CalculationSamplingPlanPlanGuard } from './approaches/calculation/sampling-plan/plan/plan.guard';
import { ReconciliationGuard as CalculationSamplingPlanReconciliationGuard } from './approaches/calculation/sampling-plan/reconciliation/reconciliation.guard';
import { SamplingPlanGuard as CalculationSamplingPlanGuard } from './approaches/calculation/sampling-plan/sampling-plan.guard';
import { CategorySummaryGuard as FallbackCategoryTierCategorySummaryGuard } from './approaches/fallback/category-tier/category/summary/category-summary.guard';
import { CategoryTierGuard as FallbackCategoryTierGuard } from './approaches/fallback/category-tier/category-tier.guard';
import { DeleteGuard as FallbackCategoryTierDeleteGuard } from './approaches/fallback/category-tier/delete/delete.guard';
import { WizardStepGuard as InherentCO2WizardStepGuard } from './approaches/inherent-co2/guards/wizard-step.guard';
import { CategorySummaryGuard as MeasurementCategoryTierCategorySummaryGuard } from './approaches/measurement/category-tier/category/summary/category-summary.guard';
import { TransferredCO2DetailsGuard as MeasurementCategoryTierTransferredCO2DetailsGuard } from './approaches/measurement/category-tier/category/transferred-co2-details/transferred-co2-details.guard';
import { CategoryTierGuard as MeasurementCategoryTierGuard } from './approaches/measurement/category-tier/category-tier.guard';
import { DeleteGuard as MeasurementCategoryTierDeleteGuard } from './approaches/measurement/category-tier/delete/delete.guard';
import { CategorySummaryGuard as N2oCategoryTierCategorySummaryGuard } from './approaches/n2o/category-tier/category/summary/category-summary.guard';
import { TransferredN2ODetailsGuard as N2oCategoryTierTransferredCO2DetailsGuard } from './approaches/n2o/category-tier/category/transferred-n2o-details/transferred-n2o-details.guard';
import { CategoryTierGuard as N2oCategoryTierGuard } from './approaches/n2o/category-tier/category-tier.guard';
import { DeleteGuard as N2oCategoryTierDeleteGuard } from './approaches/n2o/category-tier/delete/delete.guard';
import { ActivityDataGuard as PfcCategoryTierActivityDataGuard } from './approaches/pfc/category-tier/activity-data/activity-data.guard';
import { AnswersGuard as PfcCategoryTierActivityDataAnswersGuard } from './approaches/pfc/category-tier/activity-data/answers/answers.guard';
import { CategorySummaryGuard as PfcCategoryTierCategorySummaryGuard } from './approaches/pfc/category-tier/category/summary/category-summary.guard';
import { CategoryTierGuard as PfcCategoryTierGuard } from './approaches/pfc/category-tier/category-tier.guard';
import { DeleteGuard as PfcCategoryTierDeleteGuard } from './approaches/pfc/category-tier/delete/delete.guard';
import { AnswersGuard as PfcCategoryTierEmissionFactorAnswersGuard } from './approaches/pfc/category-tier/emission-factor/answers/answers.guard';
import { EmissionFactorGuard as PfcCategoryTierEmissionFactorGuard } from './approaches/pfc/category-tier/emission-factor/emission-factor.guard';
import { AnswersGuard as PfcEmissionFactorAnswersGuard } from './approaches/pfc/emission-factor/answers/answers.guard';
import { EmissionFactorGuard as PfcEmissionFactorGuard } from './approaches/pfc/emission-factor/emission-factor.guard';
import { AnswersGuard as TransferredCO2AndN2OPipelineAnswersGuard } from './approaches/transferred-co2/pipeline/answers/answers.guard';
import { PipelineGuard } from './approaches/transferred-co2/pipeline/pipeline.guard';
import { AnswersComponent } from './approaches/uncertainty-analysis/answers/answers.component';
import { AnswersGuard as UncertaintyAnalysisAnswersGuard } from './approaches/uncertainty-analysis/answers/answers.guard';
import { SummaryComponent as UncertaintyAnalysisSummaryComponent } from './approaches/uncertainty-analysis/summary/summary.component';
import { UncertaintyAnalysisComponent } from './approaches/uncertainty-analysis/uncertainty-analysis.component';
import { UncertaintyAnalysisGuard } from './approaches/uncertainty-analysis/uncertainty-analysis.guard';
import { UploadFileComponent } from './approaches/uncertainty-analysis/upload-file/upload-file.component';
import { UploadFileGuard as UncertaintyAnalysisUploadFileGuard } from './approaches/uncertainty-analysis/upload-file/upload-file.guard';
import { ConfidentialityStatementComponent } from './confidentiality-statement/confidentiality-statement.component';
import { ConfidentialityStatementSummaryComponent } from './confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary.component';
import { DetailsComponent } from './details/details.component';
import { EmissionPointDeleteComponent } from './emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from './emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointDetailsGuard } from './emission-points/emission-point-details/emission-point-details.guard';
import { EmissionPointsComponent } from './emission-points/emission-points.component';
import { EmissionPointsSummaryComponent } from './emission-points/emission-points-summary/emission-points-summary.component';
import { EmissionSourceDeleteComponent } from './emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from './emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourceDetailsGuard } from './emission-sources/emission-source-details/emission-source-details.guard';
import { EmissionSourcesComponent } from './emission-sources/emission-sources.component';
import { EmissionSourcesSummaryComponent } from './emission-sources/emission-sources-summary/emission-sources-summary.component';
import { EmissionSummariesComponent } from './emission-summaries/emission-summaries.component';
import { EmissionSummariesSummaryComponent } from './emission-summaries/emission-summaries-summary/emission-summaries-summary.component';
import { EmissionSummariesSummaryGuard } from './emission-summaries/emission-summaries-summary/emission-summaries-summary.guard';
import { EmissionSummaryDeleteComponent } from './emission-summaries/emission-summary-delete/emission-summary-delete.component';
import { EmissionSummaryDetailsComponent } from './emission-summaries/emission-summary-details/emission-summary-details.component';
import { EmissionSummaryDetailsGuard } from './emission-summaries/emission-summary-details/emission-summary-details.guard';
import { EnvironmentalSystemComponent } from './environmental-system/environmental-system.component';
import { EnvironmentalSystemSummaryComponent } from './environmental-system/environmental-system-summary/environmental-system-summary.component';
import { CalculatingCo2Co2eDetailsComponent } from './estimated-emissions/calculating-co2-co2e-details/calculating-co2-co2e-details.component';
import { EstimatedEmissionsComponent } from './estimated-emissions/estimated-emissions.component';
import { EstimatedEmissionsSummaryComponent } from './estimated-emissions/estimated-emissions-summary/estimated-emissions-summary.component';
import { InstallationDescriptionComponent } from './installation-description/installation-description.component';
import { InstallationDescriptionSummaryComponent } from './installation-description/installation-description-summary/installation-description-summary.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { ManagementProceduresBodyPipe } from './management-procedures/management-procedures-body.pipe';
import { ManagementProceduresHeadingPipe } from './management-procedures/management-procedures-heading.pipe';
import { ManagementProceduresSummaryComponent } from './management-procedures/management-procedures-summary/management-procedures-summary.component';
import { MeasurementDeviceDeleteComponent } from './measurement-devices/measurement-device-delete/measurement-device-delete.component';
import { MeasurementDeviceDetailsComponent } from './measurement-devices/measurement-device-details/measurement-device-details.component';
import { MeasurementDeviceDetailsGuard } from './measurement-devices/measurement-device-details/measurement-device-details.guard';
import { MeasurementDevicesComponent } from './measurement-devices/measurement-devices.component';
import { MeasurementDevicesSummaryComponent } from './measurement-devices/measurement-devices-summary/measurement-devices-summary.component';
import { AnswersComponent as MonitoringMethodologyPlanAnswersComponent } from './monitoring-methodology-plan/answers/answers.component';
import { AnswersGuard as MonitoringMethodologyPlanAnswersGuard } from './monitoring-methodology-plan/answers/answers.guard';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { MonitoringMethodologyPlanGuard } from './monitoring-methodology-plan/monitoring-methodology-plan.guard';
import { MonitoringMethodologyPlanSummaryComponent } from './monitoring-methodology-plan/summary/summary.component';
import { UploadFileComponent as MonitoringMethodologyPlanUploadFileComponent } from './monitoring-methodology-plan/upload-file/upload-file.component';
import { UploadFileGuard as MonitoringMethodologyPlanUploadFileGuard } from './monitoring-methodology-plan/upload-file/upload-file.guard';
import { MonitoringRolesComponent } from './monitoring-roles/monitoring-roles.component';
import { MonitoringRolesSummaryComponent } from './monitoring-roles/monitoring-roles-summary/monitoring-roles-summary.component';
import { OtherPermitsComponent } from './other-permits/other-permits.component';
import { OtherPermitsSummaryComponent } from './other-permits/other-permits-summary/other-permits-summary.component';
import { PermitApplicationRoutingModule } from './permit-application-routing.module';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PermitTypeSummaryComponent } from './permit-type/permit-type-summary/permit-type-summary.component';
import { PermitTypeSummaryGuard } from './permit-type/permit-type-summary/permit-type-summary.guard';
import { RegulatedActivitiesComponent } from './regulated-activities/regulated-activities.component';
import { RegulatedActivitiesSummaryComponent } from './regulated-activities/regulated-activities-summary/regulated-activities-summary.component';
import { ReturnForAmendsGuard } from './review/amend/return-for-amends/return-for-amends.guard';
import { ActivationDateGuard as DeterminationActivationDateGuard } from './review/determination/activation-date/activation-date.guard';
import { AnswersGuard as DeterminationAnswersGuard } from './review/determination/answers/answers.guard';
import { DeterminationGuard } from './review/determination/determination.guard';
import { EmissionsGuard as DeterminationEmissionsGuard } from './review/determination/emissions/emissions.guard';
import { OfficialNoticeGuard as DeterminationOfficialNoticeGuard } from './review/determination/official-notice/official-notice.guard';
import { ReasonGuard as DeterminationReasonGuard } from './review/determination/reason/reason.guard';
import { SummaryGuard as DeterminationSummaryGuard } from './review/determination/summary/summary.guard';
import { NotifyOperatorGuard } from './review/notify-operator/notify-operator.guard';
import { RecallGuard } from './review/recall/recall.guard';
import { AnswersGuard as EmissionsAnswersGuard } from './shared/emissions/answers/answers.guard';
import { EmissionsGuard } from './shared/emissions/emissions.guard';
import { SharedPermitModule } from './shared/shared-permit.module';
import { SummaryGuard } from './shared/summary.guard';
import { SiteDiagramComponent } from './site-diagram/site-diagram.component';
import { SiteDiagramSummaryComponent } from './site-diagram/site-diagram-summary/site-diagram-summary.component';
import { SourceStreamDeleteComponent } from './source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsGuard } from './source-streams/source-stream-details/source-stream-details.guard';
import { SourceStreamDetailsComponent } from './source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from './source-streams/source-streams.component';
import { SourceStreamsSummaryComponent } from './source-streams/source-streams-summary/source-streams-summary.component';

@NgModule({
  declarations: [
    AbbreviationsComponent,
    AbbreviationsSummaryComponent,
    AdditionalDocumentsComponent,
    AdditionalDocumentsSummaryComponent,
    AmendComponent,
    AmendSummaryComponent,
    AmendSummaryTemplateComponent,
    AnswersComponent,
    ApproachesAddComponent,
    ApproachesComponent,
    ApproachesHelpComponent,
    ApproachesPrepareComponent,
    ApproachesPrepareSummaryComponent,
    ApproachesPrepareTemplateComponent,
    ApproachesSummaryComponent,
    CalculatingCo2Co2eDetailsComponent,
    ConfidentialityStatementComponent,
    ConfidentialityStatementSummaryComponent,
    DetailsComponent,
    EmissionPointDeleteComponent,
    EmissionPointDetailsComponent,
    EmissionPointsComponent,
    EmissionPointsSummaryComponent,
    EmissionSourceDeleteComponent,
    EmissionSourceDetailsComponent,
    EmissionSourcesComponent,
    EmissionSourcesSummaryComponent,
    EmissionSummariesComponent,
    EmissionSummariesSummaryComponent,
    EmissionSummaryDeleteComponent,
    EmissionSummaryDetailsComponent,
    EnvironmentalSystemComponent,
    EnvironmentalSystemSummaryComponent,
    EstimatedEmissionsComponent,
    EstimatedEmissionsSummaryComponent,
    InstallationDescriptionComponent,
    InstallationDescriptionSummaryComponent,
    ManagementProceduresBodyPipe,
    ManagementProceduresComponent,
    ManagementProceduresHeadingPipe,
    ManagementProceduresSummaryComponent,
    MeasurementDeviceDeleteComponent,
    MeasurementDeviceDetailsComponent,
    MeasurementDevicesComponent,
    MeasurementDevicesSummaryComponent,
    MonitoringMethodologyPlanAnswersComponent,
    MonitoringMethodologyPlanComponent,
    MonitoringMethodologyPlanSummaryComponent,
    MonitoringMethodologyPlanUploadFileComponent,
    MonitoringRolesComponent,
    MonitoringRolesSummaryComponent,
    OtherPermitsComponent,
    OtherPermitsSummaryComponent,
    PermitTypeComponent,
    PermitTypeSummaryComponent,
    RegulatedActivitiesComponent,
    RegulatedActivitiesSummaryComponent,
    SiteDiagramComponent,
    SiteDiagramSummaryComponent,
    SourceStreamDeleteComponent,
    SourceStreamDetailsComponent,
    SourceStreamsComponent,
    SourceStreamsSummaryComponent,
    UncertaintyAnalysisComponent,
    UncertaintyAnalysisSummaryComponent,
    UploadFileComponent,
  ],
  imports: [PermitApplicationRoutingModule, SharedModule, SharedPermitModule],
  providers: [
    AmendGuard,
    AmendSummaryGuard,
    CalculationActivityDataGuard,
    CalculationActivityDataJustificationGuard,
    CalculationCategorySummaryGuard,
    CalculationCategoryTierActivityDataAnswersGuard,
    CalculationCategoryTierAnswersGuard,
    CalculationCategoryTierDeleteGuard,
    CalculationCategoryTierGuard,
    CalculationCategoryTierWizardStepGuard,
    CalculationCategoryTransferredCO2DetailsGuard,
    CalculationSamplingPlanAnswersGuard,
    CalculationSamplingPlanAppropriatenessGuard,
    CalculationSamplingPlanGuard,
    CalculationSamplingPlanPlanGuard,
    CalculationSamplingPlanReconciliationGuard,
    DeterminationActivationDateGuard,
    DeterminationAnswersGuard,
    DeterminationEmissionsGuard,
    DeterminationGuard,
    DeterminationOfficialNoticeGuard,
    DeterminationReasonGuard,
    DeterminationSummaryGuard,
    EmissionPointDetailsGuard,
    EmissionsAnswersGuard,
    EmissionsGuard,
    EmissionSourceDetailsGuard,
    EmissionSummariesSummaryGuard,
    EmissionSummaryDetailsGuard,
    FallbackCategoryTierCategorySummaryGuard,
    FallbackCategoryTierDeleteGuard,
    FallbackCategoryTierGuard,
    InherentCO2WizardStepGuard,
    MeasurementCategoryTierCategorySummaryGuard,
    MeasurementCategoryTierDeleteGuard,
    MeasurementCategoryTierGuard,
    MeasurementCategoryTierTransferredCO2DetailsGuard,
    MeasurementDeviceDetailsGuard,
    MonitoringMethodologyPlanAnswersGuard,
    MonitoringMethodologyPlanGuard,
    MonitoringMethodologyPlanUploadFileGuard,
    N2oCategoryTierCategorySummaryGuard,
    N2oCategoryTierDeleteGuard,
    N2oCategoryTierGuard,
    N2oCategoryTierTransferredCO2DetailsGuard,
    NotifyOperatorGuard,
    PermitTypeSummaryGuard,
    PfcCategoryTierActivityDataAnswersGuard,
    PfcCategoryTierActivityDataGuard,
    PfcCategoryTierCategorySummaryGuard,
    PfcCategoryTierDeleteGuard,
    PfcCategoryTierEmissionFactorAnswersGuard,
    PfcCategoryTierEmissionFactorGuard,
    PfcCategoryTierGuard,
    PfcEmissionFactorAnswersGuard,
    PfcEmissionFactorGuard,
    PipelineGuard,
    RecallGuard,
    ReturnForAmendsGuard,
    SourceStreamDetailsGuard,
    SummaryGuard,
    TransferredCO2AndN2OPipelineAnswersGuard,
    UncertaintyAnalysisAnswersGuard,
    UncertaintyAnalysisGuard,
    UncertaintyAnalysisUploadFileGuard,
  ],
})
export class PermitApplicationModule {}
