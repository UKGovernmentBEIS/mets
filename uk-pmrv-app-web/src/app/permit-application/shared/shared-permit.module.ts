import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ApproachTaskPipe } from '../approaches/approach-task.pipe';
import { SourceStreamHelpComponent } from '../approaches/approaches-help/source-stream-help.component';
import { ApproachesSummaryTemplateComponent } from '../approaches/approaches-summary/approaches-summary-template.component';
import { SummaryTemplateComponent as CalculationDescriptionSummaryTemplateComponent } from '../approaches/calculation/description/summary/summary-template.component';
import { SummaryDetailsComponent as CalculationPlanSummaryDetailsComponent } from '../approaches/calculation/sampling-plan/summary-details/summary-details.component';
import { SummaryTemplateComponent as FallbackDescriptionSummaryTemplateComponent } from '../approaches/fallback/description/summary/summary-template.component';
import { InherentSummaryTemplateComponent } from '../approaches/inherent-co2/summary/inherent-summary-template.component';
import { SummaryTemplateComponent as MeasurementDescriptionSummaryTemplateComponent } from '../approaches/measurement/description/summary/summary-template.component';
import { OptionalSummaryTemplateComponent as MeasurementOptionalSummaryTemplateComponent } from '../approaches/measurement/optional/summary/optional-summary-template.component';
import { SummaryTemplateComponent as N2ODescriptionSummaryTemplateComponent } from '../approaches/n2o/description/summary/summary-template.component';
import { SummaryTemplateComponent as N2OGasSummaryTemplateComponent } from '../approaches/n2o/gas/summary/summary-template.component';
import { SummaryTemplateComponent as PfcDescriptionSummaryTemplateComponent } from '../approaches/pfc/description/summary/summary-template.component';
import { SummaryDetailsComponent as Tier2EmissionFactorSummaryDetailsComponent } from '../approaches/pfc/emission-factor/summary/summary-details.component';
import { SummaryTemplateComponent as PfcTypesSummaryTemplateComponent } from '../approaches/pfc/types/summary/summary-template.component';
import { SummaryDetailsComponent as UncertaintyAnalysisSummaryDetailsComponent } from '../approaches/uncertainty-analysis/summary/summary-details.component';
import { RegulatedActivityPipe } from '../emission-summaries/emission-summaries-summary/regulated-activity.pipe';
import { SourceStreamPipe } from '../emission-summaries/emission-summaries-summary/source-stream.pipe';
import { EnvironmentalSystemSummaryTemplateComponent } from '../environmental-system/environmental-system-summary/environmental-system-summary-template.component';
import { EmissionsSummaryTemplateComponent } from '../estimated-emissions/estimated-emissions-summary/emissions-summary-template.component';
import { DescriptionSummaryTemplateComponent } from '../installation-description/installation-description-summary/description-summary-template.component';
import { IsDataFlowActivitiesPipe } from '../management-procedures/management-procedures-summary/is-data-flow-activities.pipe';
import { ManagementProceduresSummaryTemplateComponent } from '../management-procedures/management-procedures-summary/management-procedures-summary-template.component';
import { MeasurementDevicesTypePipe } from '../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';
import { MonitoringMethodologyPlanSummaryDetailsComponent } from '../monitoring-methodology-plan/summary/summary-details.component';
import { MonitoringRolesSummaryTemplateComponent } from '../monitoring-roles/monitoring-roles-summary/monitoring-roles-summary-template.component';
import { PermitsSummaryTemplateComponent } from '../other-permits/other-permits-summary/permits-summary-template.component';
import { PermitTypeSummaryTemplateComponent } from '../permit-type/permit-type-summary/permit-type-summary-template.component';
import { RegulatedActivitiesSummaryTemplateComponent } from '../regulated-activities/regulated-activities-summary/regulated-activities-summary-template.component';
import { SiteDiagramSummaryTemplateComponent } from '../site-diagram/site-diagram-summary/site-diagram-summary-template.component';
import { ApproachReturnLinkComponent } from './approach-return-link/approach-return-link.component';
import { AppliedStandardFormComponent } from './approaches/applied-standard-form.component';
import { AppliedStandardFormSummaryComponent } from './approaches/applied-standard-form-summary.component';
import { ReviewGroupDecisionComponent } from './decision/review-group-decision.component';
import { AnswersComponent } from './emissions/answers/answers.component';
import { EmissionsComponent } from './emissions/emissions.component';
import { JustificationComponent } from './emissions/justification/justification.component';
import { MeasuredEmissionsOverviewComponent } from './emissions/overview/measured-emissions-overview.component';
import { SummaryComponent } from './emissions/summary/summary.component';
import { ListReturnLinkComponent } from './list-return-link/list-return-link.component';
import { PermitTaskComponent } from './permit-task/permit-task.component';
import { PermitTaskReviewComponent } from './permit-task-review/permit-task-review.component';
import { EmissionPointPipe } from './pipes/emission-point.pipe';
import { EmissionPointCategoryNamePipe } from './pipes/emission-point-category-name.pipe';
import { EmissionSourcePipe } from './pipes/emission-source.pipe';
import { FindEmissionPointPipe } from './pipes/find-emission-point.pipe';
import { FindSourceStreamPipe } from './pipes/find-source-stream.pipe';
import { InstallationCategoryTypePipe } from './pipes/installation-category-type.pipe';
import { MeasurementDevicesLabelPipe } from './pipes/measurement-devices-label.pipe';
import { MeasurementDeviceOrMethodPipe } from './pipes/measurement-devices-or-methods.pipe';
import { MeasurementDeviceOrMethodNamePipe } from './pipes/measurement-devices-or-methods-name.pipe';
import { MeteringUncertaintyNamePipe } from './pipes/metering-uncertainty-name.pipe';
import { PermitRequestTypePipe } from './pipes/permit-request-type.pipe';
import { ReviewGroupPipe } from './pipes/review-group.pipe';
import { ReviewGroupStatusWrapperPipe } from './pipes/review-group-status-wrapper.pipe';
import { SamplingFrequencyPipe } from './pipes/sampling-frequency.pipe';
import { SourceStreamCategoryNamePipe } from './pipes/source-stream-category-name.pipe';
import { TaskPipe } from './pipes/task.pipe';
import { TaskProcedureFormPipe } from './pipes/task-procedure-form.pipe';
import { TaskProcedureOptionalFormPipe } from './pipes/task-procedure-optional-form.pipe';
import { TaskStatusPipe } from './pipes/task-status.pipe';
import { TierEmissionPointNamePipe } from './pipes/tier-emission-point-name.pipe';
import { TierSourceStreamNamePipe } from './pipes/tier-source-stream-name.pipe';
import { ProcedureFormComponent } from './procedure-form/procedure-form.component';
import { ProcedureFormSummaryComponent } from './procedure-form-summary/procedure-form-summary.component';
import { ReviewDeterminationSummaryDetailsComponent } from './review-determination-summary-details/summary-details.component';
import { ReviewSectionsComponent } from './review-sections/review-sections.component';
import { SectionsComponent } from './sections/sections.component';
import { SiteEmissionsComponent } from './site-emissions/site-emissions.component';
import { SiteEmissionsPercentagePipe } from './site-emissions/site-emissions-percentage.pipe';
import { TransferredCo2PipelineSummaryTemplateComponent } from './transferred-co2-pipeline-summary-template/transferred-co2-pipeline-summary-template.component';

const declarations = [
  RegulatedActivityPipe,
  AppliedStandardFormComponent,
  AppliedStandardFormSummaryComponent,
  ListReturnLinkComponent,
  MeasurementDevicesTypePipe,
  SourceStreamPipe,
  ApproachReturnLinkComponent,
  ApproachTaskPipe,
  PermitTaskComponent,
  PermitTaskReviewComponent,
  TaskPipe,
  TaskProcedureFormPipe,
  TaskProcedureOptionalFormPipe,
  TaskStatusPipe,
  ProcedureFormComponent,
  ProcedureFormSummaryComponent,
  EmissionSourcePipe,
  EmissionPointPipe,
  FindSourceStreamPipe,
  FindEmissionPointPipe,
  SourceStreamCategoryNamePipe,
  EmissionPointCategoryNamePipe,
  TierSourceStreamNamePipe,
  TierEmissionPointNamePipe,
  AnswersComponent,
  MeasuredEmissionsOverviewComponent,
  SummaryComponent,
  SamplingFrequencyPipe,
  MeasurementDevicesLabelPipe,
  TransferredCo2PipelineSummaryTemplateComponent,

  JustificationComponent,
  EmissionsComponent,
  PermitsSummaryTemplateComponent,
  PermitTypeSummaryTemplateComponent,
  RegulatedActivitiesSummaryTemplateComponent,
  InstallationCategoryTypePipe,
  DescriptionSummaryTemplateComponent,
  EmissionsSummaryTemplateComponent,
  MonitoringMethodologyPlanSummaryDetailsComponent,
  SiteDiagramSummaryTemplateComponent,

  InherentSummaryTemplateComponent,
  IsDataFlowActivitiesPipe,
  ManagementProceduresSummaryTemplateComponent,
  MonitoringRolesSummaryTemplateComponent,
  EnvironmentalSystemSummaryTemplateComponent,
  ApproachesSummaryTemplateComponent,
  ReviewGroupPipe,
  MeasurementDescriptionSummaryTemplateComponent,
  MeasurementDevicesTypePipe,
  MeasurementOptionalSummaryTemplateComponent,
  N2ODescriptionSummaryTemplateComponent,
  N2OGasSummaryTemplateComponent,
  PfcDescriptionSummaryTemplateComponent,
  PfcTypesSummaryTemplateComponent,
  SiteEmissionsComponent,
  SiteEmissionsPercentagePipe,
  FallbackDescriptionSummaryTemplateComponent,
  Tier2EmissionFactorSummaryDetailsComponent,
  MeasurementDeviceOrMethodNamePipe,
  MeasurementDeviceOrMethodPipe,
  MeteringUncertaintyNamePipe,
  CalculationDescriptionSummaryTemplateComponent,
  CalculationPlanSummaryDetailsComponent,
  SourceStreamHelpComponent,
  UncertaintyAnalysisSummaryDetailsComponent,
  SectionsComponent,
  PermitRequestTypePipe,
  ReviewDeterminationSummaryDetailsComponent,
  ReviewSectionsComponent,
  ReviewGroupStatusWrapperPipe,
  ReviewGroupDecisionComponent,
];

@NgModule({
  declarations: declarations,
  exports: declarations,
  imports: [RouterModule, SharedModule],
  providers: [
    ApproachTaskPipe,
    FindEmissionPointPipe,
    FindSourceStreamPipe,
    MeasurementDevicesTypePipe,
    ReviewGroupStatusWrapperPipe,
    TaskStatusPipe,
  ],
})
export class SharedPermitModule {}
