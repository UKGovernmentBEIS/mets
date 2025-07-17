import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { AbbreviationsComponent } from './abbreviations/abbreviations.component';
import { AbbreviationsSummaryComponent } from './abbreviations/abbreviations-summary/abbreviations-summary.component';
import { AdditionalDocumentsComponent } from './additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryComponent } from './additional-documents/additional-documents-summary/additional-documents-summary.component';
import { AmendComponent } from './amend/amend.component';
import { AmendGuard } from './amend/amend.guard';
import { AmendSummaryComponent } from './amend/summary/amend-summary.component';
import { AmendSummaryGuard } from './amend/summary/amend-summary.guard';
import { ApproachesComponent } from './approaches/approaches.component';
import { ApproachesAddComponent } from './approaches/approaches-add/approaches-add.component';
import { ApproachesHelpComponent } from './approaches/approaches-help/approaches-help.component';
import { SourceStreamHelpComponent } from './approaches/approaches-help/source-stream-help.component';
import { ApproachesPrepareComponent } from './approaches/approaches-prepare/approaches-prepare.component';
import { ApproachesPrepareSummaryComponent } from './approaches/approaches-prepare/approaches-prepare-summary/approaches-prepare-summary.component';
import { ApproachesSummaryComponent } from './approaches/approaches-summary/approaches-summary.component';
import { AnswersComponent } from './approaches/uncertainty-analysis/answers/answers.component';
import { AnswersGuard } from './approaches/uncertainty-analysis/answers/answers.guard';
import { SummaryComponent as UncertaintyAnalysisSummaryComponent } from './approaches/uncertainty-analysis/summary/summary.component';
import { UncertaintyAnalysisComponent } from './approaches/uncertainty-analysis/uncertainty-analysis.component';
import { UncertaintyAnalysisGuard } from './approaches/uncertainty-analysis/uncertainty-analysis.guard';
import { UploadFileComponent } from './approaches/uncertainty-analysis/upload-file/upload-file.component';
import { UploadFileGuard } from './approaches/uncertainty-analysis/upload-file/upload-file.guard';
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
import { ManagementProceduresSummaryComponent } from './management-procedures/management-procedures-summary/management-procedures-summary.component';
import { MeasurementDeviceDeleteComponent } from './measurement-devices/measurement-device-delete/measurement-device-delete.component';
import { MeasurementDeviceDetailsComponent } from './measurement-devices/measurement-device-details/measurement-device-details.component';
import { MeasurementDeviceDetailsGuard } from './measurement-devices/measurement-device-details/measurement-device-details.guard';
import { MeasurementDevicesComponent } from './measurement-devices/measurement-devices.component';
import { MeasurementDevicesSummaryComponent } from './measurement-devices/measurement-devices-summary/measurement-devices-summary.component';
import { ElectricityFlowsComponent } from './mmp-energy-flows/electricity-flows/electricity-flows.component';
import { FuelInputFlowsComponent } from './mmp-energy-flows/fuel-input-flows.component';
import { MeasurableHeatFlowsComponent } from './mmp-energy-flows/measurable-heat-flows/measurable-heat-flows.component';
import { MMPEnergyFlowsStepGuard } from './mmp-energy-flows/mmp-energy-flows-summary/mmp-energy-flows-step-guard';
import { MmpEnergyFlowsSummaryComponent } from './mmp-energy-flows/mmp-energy-flows-summary/mmp-energy-flows-summary.component';
import { MMPEnergyFlowsSummaryGuard } from './mmp-energy-flows/mmp-energy-flows-summary/mmp-energy-flows-summary-guard';
import { WasteGasFlowsComponent } from './mmp-energy-flows/waste-gas-flows/waste-gas-flows.component';
import { MmpInstallationDescriptionAnswersComponent } from './mmp-installation-description/answers/mmp-installation-description-answers.component';
import { ConnectionDeleteComponent } from './mmp-installation-description/connection-delete/connection-delete.component';
import { ConnectionDetailsComponent } from './mmp-installation-description/connection-details/connection-details.component';
import { ConnectionDetailsGuard } from './mmp-installation-description/connection-details/connection-details.guard';
import { ConnectionsComponent } from './mmp-installation-description/connections/connections.component';
import { MmpFlowDiagramComponent } from './mmp-installation-description/mmp-flow-diagram/mmp-flow-diagram.component';
import { MmpInstallationDescriptionComponent } from './mmp-installation-description/mmp-installation-description.component';
import { MMPInstallationDescriptionSummaryGuard } from './mmp-installation-description/summary/mmp-installation-description-summary-guard';
import { AddPhysicalPartComponent } from './mmp-methods/add-physical-part/add-physical-part.component';
import { AvoidDoubleCountComponent } from './mmp-methods/avoid-double-count/avoid-double-count.component';
import { DeletePhysicalPartComponent } from './mmp-methods/delete-physical-part/delete-physical-part.component';
import { MMPMethodsStepGuard } from './mmp-methods/methods-step-guard';
import { MethodsSummaryComponent } from './mmp-methods/methods-summary/methods-summary.component';
import { MethodsToAssignPartsComponent } from './mmp-methods/methods-to-assign-parts/methods-to-assign-parts.component';
import { methodsBacklinkResolver } from './mmp-methods/mmp-methods';
import { PhysicalPartsGuardQuestionComponent } from './mmp-methods/physical-parts-guard-question.component';
import { PhysicalPartsListComponent } from './mmp-methods/physical-parts-list/physical-parts-list.component';
import { AssignmentOfResponsibilitiesComponent } from './mmp-procedures/assignment-of-responsibilities/assignment-of-responsibilities.component';
import { ControlActivitiesComponent } from './mmp-procedures/control-activities/control-activities.component';
import { DataFlowActivitiesComponent } from './mmp-procedures/data-flow-activities/data-flow-activities.component';
import { MMPProceduresStepGuard } from './mmp-procedures/mmp-procedures-summary/mmp-procedures-step-guard';
import { MmpProceduresSummaryComponent } from './mmp-procedures/mmp-procedures-summary/mmp-procedures-summary.component';
import { MMPProceduresSummaryGuard } from './mmp-procedures/mmp-procedures-summary/mmp-procedures-summary-guard';
import { MonitoringPlanAppropriatenessComponent } from './mmp-procedures/monitoring-plan-appropriateness/monitoring-plan-appropriateness.component';
import { AnnualProductionLevelComponent } from './mmp-sub-installations/annual-production-levels/annual-production-levels.component';
import { DeleteSubInstallationComponent } from './mmp-sub-installations/delete-sub-installation/delete-sub-installation.component';
import { DirectlyAttributableEmissionsComponent } from './mmp-sub-installations/directly-attributable-emissions/directly-attributable-emissions.component';
import { ExchangeabilityComponent } from './mmp-sub-installations/exchangeability/exchangeability.component';
import { AnnualActivityLevelsFuelComponent } from './mmp-sub-installations/fallback/annual-activity-levels-fuel/annual-activity-levels-fuel.component';
import { AnnualActivityLevelsHeatComponent } from './mmp-sub-installations/fallback/annual-activity-levels-heat/annual-activity-levels-heat.component';
import { AnnualActivityLevelsProcessComponent } from './mmp-sub-installations/fallback/annual-activity-levels-process/annual-activity-levels-process.component';
import { DirectlyAttributableEmissionsFAComponent } from './mmp-sub-installations/fallback/directly-attributable-emissions/directly-attributable-emissions.component';
import { FuelInputRelevantEmissionFactorFAComponent } from './mmp-sub-installations/fallback/fuel-input-relevant-emission-factor/fuel-input-relevant-emission-factor.component';
import { MeasurableHeatExportedComponent } from './mmp-sub-installations/fallback/measurable-heat-exported/measurable-heat-exported.component';
import { MeasurableHeatImportedComponent } from './mmp-sub-installations/fallback/measurable-heat-imported/measurable-heat-imported.component';
import { MeasurableHeatProducedComponent } from './mmp-sub-installations/fallback/measurable-heat-produced/measurable-heat-produced.component';
import { MMPSubInstallationsFallbackStepGuard } from './mmp-sub-installations/fallback/sub-installations-fallback-summary/sub-installations-fallback-step-guard';
import { SubInstallationsFallbackSummaryComponent } from './mmp-sub-installations/fallback/sub-installations-fallback-summary/sub-installations-fallback-summary.component';
import { MMPSubInstallationsFallbackSummaryGuard } from './mmp-sub-installations/fallback/sub-installations-fallback-summary/sub-installations-fallback-summary-guard';
import { FuelInputRelevantEmissionFactorComponent } from './mmp-sub-installations/fuel-input-relevant-emission-factor/fuel-input-relevant-emission-factor.component';
import { ImportedExportedMeasurableHeatComponent } from './mmp-sub-installations/imported-exported-measurable-heat/imported-exported-measurable-heat.component';
import { ImportedMeasureableHeatFlowsComponent } from './mmp-sub-installations/imported-measureable-heat-flows/imported-measureable-heat-flows.component';
import { MmpSubInstallationsComponent } from './mmp-sub-installations/mmp-sub-installations.component';
import {
  resolveDirectlyAttributableEmissionsFABackLink,
  resolveImportedMeasureableHeatFlowsBackLink,
} from './mmp-sub-installations/mmp-sub-installations-status';
import { CalculationAromaticsComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-aromatics/calculation-aromatics.component';
import { CalculationDolimeComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-dolime/calculation-dolime.component';
import { CalculationEthyleneOxideGlycolsComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-ethylene-oxide-glycols/calculation-ethylene-oxide-glycols.component';
import { CalculationHydrogenComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-hydrogen/calculation-hydrogen.component';
import { CalculationLimeComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-lime/calculation-lime.component';
import { CalculationRefineryProductsComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-refinery-products/calculation-refinery-products.component';
import { CalculationSteamCrackingComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-steam-cracking/calculation-steam-cracking.component';
import { CalculationSynthesisGasComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-synthesis-gas/calculation-synthesis-gas.component';
import { CalculationVinylChlorideMonomerComponent } from './mmp-sub-installations/special-product-benchmarks/calculation-vinyl-chloride-monomer/calculation-vinyl-chloride-monomer.component';
import { SubInstallationDetailsComponent } from './mmp-sub-installations/sub-installation-details/sub-installation-details.component';
import { SubInstallationFallbackDetailsComponent } from './mmp-sub-installations/sub-installation-fallback-details/sub-installation-fallback-details.component';
import { MMPSubInstallationsStepGuard } from './mmp-sub-installations/sub-installations-summary/sub-installations-step-guard';
import { SubInstallationsSummaryComponent } from './mmp-sub-installations/sub-installations-summary/sub-installations-summary.component';
import { MMPSubInstallationsSummaryGuard } from './mmp-sub-installations/sub-installations-summary/sub-installations-summary-guard';
import { WasteGasBalanceComponent } from './mmp-sub-installations/waste-gas-balance/waste-gas-balance.component';
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
import { PermitRoute } from './permit-route.interface';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PermitTypeSummaryComponent } from './permit-type/permit-type-summary/permit-type-summary.component';
import { PermitTypeSummaryGuard } from './permit-type/permit-type-summary/permit-type-summary.guard';
import { RegulatedActivitiesComponent } from './regulated-activities/regulated-activities.component';
import { RegulatedActivitiesSummaryComponent } from './regulated-activities/regulated-activities-summary/regulated-activities-summary.component';
import { SummaryGuard } from './shared/summary.guard';
import { SiteDiagramComponent } from './site-diagram/site-diagram.component';
import { SiteDiagramSummaryComponent } from './site-diagram/site-diagram-summary/site-diagram-summary.component';
import { SourceStreamDeleteComponent } from './source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsGuard } from './source-streams/source-stream-details/source-stream-details.guard';
import { SourceStreamDetailsComponent } from './source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from './source-streams/source-streams.component';
import { SourceStreamsSummaryComponent } from './source-streams/source-streams-summary/source-streams-summary.component';

const permitRoutes: PermitRoute[] = [
  {
    path: 'review',
    loadChildren: () => import('./review/review.module').then((m) => m.ReviewModule),
  },
  {
    path: 'permit-type',
    data: { permitTask: 'permitType' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Permit type' },
        component: PermitTypeComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Permit type - Summary page', breadcrumb: 'Permit type' },
        component: PermitTypeSummaryComponent,
        canActivate: [PermitTypeSummaryGuard],
      },
    ],
  },
  {
    path: 'details/summary',
    data: { pageTitle: 'Installation and operator details', breadcrumb: true },
    component: DetailsComponent,
  },
  {
    path: 'other-permits',
    data: { permitTask: 'environmentalPermitsAndLicences' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Other environmental permits or licences' },
        component: OtherPermitsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Other environmental permits or licences - Summary page',
          breadcrumb: 'Other environmental permits or licences',
        },
        component: OtherPermitsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'description',
    data: { permitTask: 'installationDescription' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Description of the installation' },
        component: InstallationDescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Description of the installation - Summary page',
          breadcrumb: 'Description of the installation',
        },
        component: InstallationDescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'regulated-activities',
    data: { permitTask: 'regulatedActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Select the regulated activities that happen at the installation' },
        component: RegulatedActivitiesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'All the regulated activities carried out at the installation - Summary page',
          breadcrumb: 'Regulated activities',
        },
        component: RegulatedActivitiesSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'category',
    data: { permitTask: 'estimatedAnnualEmissions' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Estimated annual CO2e' },
        component: EstimatedEmissionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Estimated annual CO2e - Summary page', breadcrumb: 'Estimated annual CO2e' },
        component: EstimatedEmissionsSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: 'calculating-details',
        data: { pageTitle: 'Calculating CO2 and CO2e - Details page' },
        component: CalculatingCo2Co2eDetailsComponent,
      },
    ],
  },
  {
    path: 'monitoring-roles',
    data: { permitTask: 'monitoringReporting' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring and reporting roles' },
        component: MonitoringRolesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring and reporting roles - Summary page',
          breadcrumb: 'Monitoring and reporting roles',
        },
        component: MonitoringRolesSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'responsibilities',
    data: { permitTask: 'assignmentOfResponsibilities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Assignment of responsibilities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Assignment of responsibilities - Summary page',
          breadcrumb: 'Assignment of responsibilities',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'appropriateness',
    data: { permitTask: 'monitoringPlanAppropriateness' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring plan appropriateness' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring plan appropriateness - Summary page',
          breadcrumb: 'Monitoring plan appropriateness',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'data-flow-activities',
    data: { permitTask: 'dataFlowActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Data flow activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Data flow activities - Summary page', breadcrumb: 'Data flow activities' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'review-validation',
    data: { permitTask: 'reviewAndValidationOfData' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Review and validation of data' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Review and validation of data - Summary page',
          breadcrumb: 'Review and validation of data',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'environmental-system',
    data: { permitTask: 'environmentalManagementSystem' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Environmental management system' },
        component: EnvironmentalSystemComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Environmental management system - Summary page',
          breadcrumb: 'Environmental management system',
        },
        component: EnvironmentalSystemSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'outsourced-activities',
    data: { permitTask: 'controlOfOutsourcedActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Control of outsourced activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Control of outsourced activities - Summary page',
          breadcrumb: 'Control of outsourced activities',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'qa-activities',
    data: { permitTask: 'qaDataFlowActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Quality assurance of IT used for data flow activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Quality assurance of IT used for data flow activities - Summary page',
          breadcrumb: 'Quality assurance of IT used for data flow activities',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'qa-metering-measuring',
    data: { permitTask: 'qaMeteringAndMeasuringEquipment' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Quality assurance of metering and measuring equipment' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Quality assurance of metering and measuring equipment - Summary page',
          breadcrumb: 'Quality assurance of metering and measuring equipment',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'assess-control-risk',
    data: { permitTask: 'assessAndControlRisk' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Assessing and controlling risks' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Assessing and controlling risks - Summary page',
          breadcrumb: 'Assessing and controlling risks',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'corrections',
    data: { permitTask: 'correctionsAndCorrectiveActions' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Corrections and corrective actions' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Corrections and corrective actions - Summary page',
          breadcrumb: 'Corrections and corrective actions',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'record-keeping',
    data: { permitTask: 'recordKeepingAndDocumentation' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Record keeping and documentation' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Record keeping and documentation - Summary page',
          breadcrumb: 'Record keeping and documentation',
        },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'source-streams',
    data: { permitTask: 'sourceStreams' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Source streams (fuels and materials)' },
        component: SourceStreamsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add a source stream', backlink: '../' },
        component: SourceStreamDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:streamId',
        data: { pageTitle: 'Are you sure you want to delete this source stream?', backlink: '../..' },
        component: SourceStreamDeleteComponent,
        canActivate: [SourceStreamDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Source streams (fuels and materials) - Summary page', breadcrumb: 'Source streams' },
        component: SourceStreamsSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':streamId',
        data: { pageTitle: 'Edit source stream', backlink: '../' },
        component: SourceStreamDetailsComponent,
        canActivate: [SourceStreamDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'emission-sources',
    data: { permitTask: 'emissionSources' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Emission sources' },
        component: EmissionSourcesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add an emission source', backlink: '../' },
        component: EmissionSourceDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:sourceId',
        data: { pageTitle: 'Are you sure you want to delete this emission source?', backlink: '../..' },
        component: EmissionSourceDeleteComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Emission sources - Summary page', breadcrumb: 'Emission sources' },
        component: EmissionSourcesSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':sourceId',
        data: { pageTitle: 'Edit emission source', backlink: '../' },
        component: EmissionSourceDetailsComponent,
        canActivate: [EmissionSourceDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'emission-points',
    data: { permitTask: 'emissionPoints' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Emission points' },
        component: EmissionPointsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add an emission point', backlink: '../' },
        component: EmissionPointDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:emissionPointId',
        data: { pageTitle: 'Are you sure you want to delete this emission point?', backlink: '../..' },
        component: EmissionPointDeleteComponent,
        canActivate: [EmissionPointDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Emission points - Summary page', breadcrumb: 'Emission points' },
        component: EmissionPointsSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':emissionPointId',
        data: { pageTitle: 'Edit emission point', backlink: '../' },
        component: EmissionPointDetailsComponent,
        canActivate: [EmissionPointDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'emission-summaries',
    data: { permitTask: 'emissionSummaries' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions summaries and regulated activities' },
        component: EmissionSummariesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add an emission summary', backlink: '../' },
        component: EmissionSummaryDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:emissionSummaryIndex',
        data: { pageTitle: 'Are you sure you want to delete this emission summary?', backlink: '../..' },
        component: EmissionSummaryDeleteComponent,
        canActivate: [EmissionSummaryDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Emissions summaries and regulated activities - Summary page',
          breadcrumb: 'Emission summaries',
        },
        component: EmissionSummariesSummaryComponent,
        canActivate: [SummaryGuard, EmissionSummariesSummaryGuard],
      },
      {
        path: ':emissionSummaryIndex',
        data: { pageTitle: 'Edit emission summary', backlink: '../' },
        component: EmissionSummaryDetailsComponent,
        canActivate: [EmissionSummaryDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'measurement-devices',
    data: { permitTask: 'measurementDevicesOrMethods' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Measurement devices or methods' },
        component: MeasurementDevicesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add a measurement device or method', backlink: '../' },
        component: MeasurementDeviceDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:deviceId',
        data: { pageTitle: 'Are you sure you want to delete this measurement device or method?', backlink: '../..' },
        component: MeasurementDeviceDeleteComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Measurement devices or methods - Summary page', breadcrumb: 'Measurement devices' },
        component: MeasurementDevicesSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':deviceId',
        data: { pageTitle: 'Edit measurement device or method', backlink: '../' },
        component: MeasurementDeviceDetailsComponent,
        canActivate: [MeasurementDeviceDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'site-diagram',
    data: { permitTask: 'siteDiagrams' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Site diagram' },
        component: SiteDiagramComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Site diagram - Summary page', breadcrumb: 'Site diagram' },
        component: SiteDiagramSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'confidentiality-statement',
    data: { permitTask: 'confidentialityStatement' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Confidentiality statement' },
        component: ConfidentialityStatementComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Confidentiality statement - Summary page', breadcrumb: 'Confidentiality statement' },
        component: ConfidentialityStatementSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'abbreviations',
    data: { permitTask: 'abbreviations' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Definitions of abbreviations, acronyms and terminology' },
        component: AbbreviationsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Definitions of abbreviations, acronyms and terminology - Summary page',
          breadcrumb: 'Abbreviations',
        },
        component: AbbreviationsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'monitoring-methodology-plan',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring Methodology Plan' },
        component: MonitoringMethodologyPlanComponent,
        canActivate: [MonitoringMethodologyPlanGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-file',
        data: { pageTitle: 'Monitoring Methodology Plan - Upload file' },
        component: MonitoringMethodologyPlanUploadFileComponent,
        canActivate: [MonitoringMethodologyPlanUploadFileGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Monitoring Methodology Plan - Confirm answers' },
        component: MonitoringMethodologyPlanAnswersComponent,
        canActivate: [MonitoringMethodologyPlanAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring Methodology Plan - Summary page', breadcrumb: 'Monitoring Methodology Plan' },
        component: MonitoringMethodologyPlanSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'mmp-installation-description',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring Methodology Plan - Describe the installation and its main processes' },
        component: MmpInstallationDescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-file',
        data: { pageTitle: 'Monitoring Methodology Plan - Upload a flow diagram', backlink: '../' },
        component: MmpFlowDiagramComponent,
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'connections',
        data: {
          pageTitle: 'Monitoring Methodology Plan - Connections to other ETS installations or non-ETS entities',
          backlink: '../upload-file',
        },
        component: ConnectionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'connections',
        children: [
          {
            path: 'add',
            data: {
              pageTitle: 'Monitoring Methodology Plan - Add a connection to an installation or entity',
              backlink: '../',
            },
            component: ConnectionDetailsComponent,
            canActivate: [ConnectionDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':connectionIndex',
            data: {
              pageTitle: 'Monitoring Methodology Plan - Add a connection to an installation or entity',
              backlink: '../',
            },
            component: ConnectionDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:connectionIndex',
            data: {
              pageTitle: 'Monitoring Methodology Plan - Are you sure you want to delete this connection?',
              backlink: '../..',
            },
            component: ConnectionDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring Methodology Plan' },
        component: MmpInstallationDescriptionAnswersComponent,
        canActivate: [MMPInstallationDescriptionSummaryGuard],
      },
    ],
  },
  {
    path: 'mmp-sub-installations',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Sub-installations' },
        component: MmpSubInstallationsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'sub-installation-details',
        data: { pageTitle: 'Sub-installation details', backlink: '../' },
        component: SubInstallationDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'sub-installation-fallback-details',
        data: { pageTitle: 'Sub-installation fallback details', backlink: '../' },
        component: SubInstallationFallbackDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'fallback/:subInstallationNo',
        children: [
          {
            path: '',
            data: { pageTitle: 'Sub-installation fallback details', backlink: '../../' },
            component: SubInstallationFallbackDetailsComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'annual-activity-levels-heat',
            data: { pageTitle: 'Annual activity levels', backlink: '../' },
            component: AnnualActivityLevelsHeatComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'annual-activity-levels-fuel',
            data: { pageTitle: 'Annual activity levels', backlink: '../' },
            component: AnnualActivityLevelsFuelComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'annual-activity-levels-process',
            data: { pageTitle: 'Annual activity levels', backlink: '../' },
            component: AnnualActivityLevelsProcessComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'directly-attributable-emissions',
            resolve: {
              backlinkUrl: resolveDirectlyAttributableEmissionsFABackLink,
            },
            data: {
              pageTitle: 'Directly attributable emissions',
              backlink: ({ backlinkUrl }) => backlinkUrl,
            },
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            component: DirectlyAttributableEmissionsFAComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'fuel-input-relevant-emission-factor',
            data: {
              pageTitle: 'Fuel input and relevant emission factor',
              backlink: '../directly-attributable-emissions/',
            },
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            component: FuelInputRelevantEmissionFactorFAComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'measurable-heat-exported',
            data: { pageTitle: 'Measurable heat exported', backlink: '../fuel-input-relevant-emission-factor' },
            component: MeasurableHeatExportedComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'measurable-heat-produced',
            data: { pageTitle: 'Measurable heat produced', backlink: '../fuel-input-relevant-emission-factor' },
            component: MeasurableHeatProducedComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'measurable-heat-imported',
            data: { pageTitle: 'Measurable heat imported', backlink: '../measurable-heat-produced' },
            component: MeasurableHeatImportedComponent,
            canActivate: [MMPSubInstallationsFallbackStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
            },
            component: SubInstallationsFallbackSummaryComponent,
            canActivate: [MMPSubInstallationsFallbackSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: ':subInstallationNo',
        children: [
          {
            path: '',
            data: { pageTitle: 'Sub-installation details', backlink: '../' },
            component: SubInstallationDetailsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },

          {
            path: 'annual-production-level',
            data: { pageTitle: 'Annual production levels (activity)', backlink: '../' },
            component: AnnualProductionLevelComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'exchangeability',
            data: { pageTitle: 'Exchangeability of fuel and electricity', backlink: '../annual-production-level' },
            component: ExchangeabilityComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'imported-measureable-heat-flows',
            resolve: {
              backlinkUrl: resolveImportedMeasureableHeatFlowsBackLink,
            },
            data: {
              pageTitle: 'Imported measurable heat flows from non-ETS',
              backlink: ({ backlinkUrl }) => backlinkUrl,
            },
            component: ImportedMeasureableHeatFlowsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'directly-attributable-emissions',
            data: { pageTitle: 'Directly attributable emissions', backlink: '../imported-measureable-heat-flows' },
            component: DirectlyAttributableEmissionsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'fuel-input-relevant-emission-factor',
            data: {
              pageTitle: 'Fuel input and relevant emission factor',
              backlink: '../directly-attributable-emissions',
            },
            component: FuelInputRelevantEmissionFactorComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'imported-exported-measurable-heat',
            data: {
              pageTitle: 'Imported and exported measurable heat',
              backlink: '../fuel-input-relevant-emission-factor',
            },
            component: ImportedExportedMeasurableHeatComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'waste-gas-balance',
            data: {
              pageTitle: 'Waste gas balance',
              backlink: '../imported-exported-measurable-heat',
            },
            component: WasteGasBalanceComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-refinery-products',
            data: {
              pageTitle: 'Calculation of historical activity levels for refinery products',
              backlink: '../waste-gas-balance',
            },
            component: CalculationRefineryProductsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-lime',
            data: {
              pageTitle: 'Calculation of historical activity levels for lime',
              backlink: '../waste-gas-balance',
            },
            component: CalculationLimeComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-dolime',
            data: {
              pageTitle: 'Calculation of historical activity levels for dolime',
              backlink: '../waste-gas-balance',
            },
            component: CalculationDolimeComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-steam-cracking',
            data: {
              pageTitle: 'Calculation of historical activity levels for steam cracking',
              backlink: '../waste-gas-balance',
            },
            component: CalculationSteamCrackingComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-aromatics',
            data: {
              pageTitle: 'Calculation of historical activity levels for aromatics',
              backlink: '../waste-gas-balance',
            },
            component: CalculationAromaticsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-hydrogen',
            data: {
              pageTitle: 'Calculation of historical activity levels for hydrogen',
              backlink: '../waste-gas-balance',
            },
            component: CalculationHydrogenComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-synthesis-gas',
            data: {
              pageTitle: 'Calculation of historical activity levels for synthesis gas',
              backlink: '../waste-gas-balance',
            },
            component: CalculationSynthesisGasComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-ethylene-oxide-ethylene-glycols',
            data: {
              pageTitle: 'Calculation of historical activity levels for ethylene oxide and ethylene glycols',
              backlink: '../waste-gas-balance',
            },
            component: CalculationEthyleneOxideGlycolsComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'calculation-vinyl-chloride-monomer',
            data: {
              pageTitle: 'Calculation of historical activity levels for vinyl chloride monomer',
              backlink: '../waste-gas-balance',
            },
            component: CalculationVinylChlorideMonomerComponent,
            canActivate: [MMPSubInstallationsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
            },
            component: SubInstallationsSummaryComponent,
            canActivate: [MMPSubInstallationsSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete',
            data: { pageTitle: 'Are you sure you want to delete this Sub-installation?', backlink: '../..' },
            component: DeleteSubInstallationComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'mmp-methods',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: {
          pageTitle:
            'Are there any physical parts of the installation or units which serve more than one sub-installation?',
        },
        component: PhysicalPartsGuardQuestionComponent,
        canActivate: [MMPMethodsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'physical-parts-list',
        children: [
          {
            path: '',
            data: { pageTitle: 'Physical parts of the installation', backlink: '..' },
            component: PhysicalPartsListComponent,
            canActivate: [MMPMethodsStepGuard],
          },
          {
            path: 'remove/:id',
            data: { pageTitle: 'Are you sure you want to delete this physical part?', backlink: '../..' },
            component: DeletePhysicalPartComponent,
            canActivate: [MMPMethodsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add-part',
            data: {
              pageTitle: 'Add a physical part of the installation or unit that serves more than one sub-installation',
              backlink: '../',
            },
            component: AddPhysicalPartComponent,
            canActivate: [MMPMethodsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':id',
            data: {
              pageTitle: 'Add a physical part of the installation or unit that serves more than one sub-installation',
              backlink: '../',
            },
            component: AddPhysicalPartComponent,
            canActivate: [MMPMethodsStepGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'assign-parts',
        data: { pageTitle: 'Methods to assign parts of installations', backlink: '../physical-parts-list' },
        component: MethodsToAssignPartsComponent,
        canActivate: [MMPMethodsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'avoid-double-count',
        resolve: {
          backlink: methodsBacklinkResolver,
        },
        data: { pageTitle: 'Methods to assign parts of installations', backlink: '../assign-parts' },
        component: AvoidDoubleCountComponent,
        canActivate: [MMPMethodsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check your answers',
        },
        component: MethodsSummaryComponent,
        canActivate: [MMPMethodsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'mmp-procedures',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Assignment of responsibilities',
        },
        component: AssignmentOfResponsibilitiesComponent,
        canActivate: [MMPProceduresStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'monitoring-plan-appropriateness',
        data: {
          pageTitle: 'Monitoring plan appropriateness',
          backlink: '../',
        },
        component: MonitoringPlanAppropriatenessComponent,
        canActivate: [MMPProceduresStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'data-flow-activities',
        data: {
          pageTitle: 'Data flow activities',
          backlink: '../monitoring-plan-appropriateness',
        },
        component: DataFlowActivitiesComponent,
        canActivate: [MMPProceduresStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'control-activities',
        data: {
          pageTitle: 'Control activities',
          backlink: '../data-flow-activities',
        },
        component: ControlActivitiesComponent,
        canActivate: [MMPProceduresStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check your answers',
        },
        component: MmpProceduresSummaryComponent,
        canActivate: [MMPProceduresSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'mmp-energy-flows',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Fuel input flows',
        },
        component: FuelInputFlowsComponent,
        canActivate: [MMPEnergyFlowsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'measurable-heat-flows',
        data: { pageTitle: 'Measurable heat flows of imports, exports, consumption and production', backlink: '../' },
        component: MeasurableHeatFlowsComponent,
        canActivate: [MMPEnergyFlowsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'waste-gas-flows',
        data: {
          pageTitle: 'Waste gas flows of imports, exports, consumption and production',
          backlink: '../measurable-heat-flows',
        },
        component: WasteGasFlowsComponent,
        canActivate: [MMPEnergyFlowsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'electricity-flows',
        data: {
          pageTitle: 'Electricity flows of imports, exports, consumption and production',
          backlink: '../waste-gas-flows',
        },
        component: ElectricityFlowsComponent,
        canActivate: [MMPEnergyFlowsStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Check your answers',
        },
        component: MmpEnergyFlowsSummaryComponent,
        canActivate: [MMPEnergyFlowsSummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'additional-documents',
    data: { permitTask: 'additionalDocuments' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Additional documents and information' },
        component: AdditionalDocumentsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Additional documents and information - Summary page', breadcrumb: 'Additional documents' },
        component: AdditionalDocumentsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'file-download/:fileType/:uuid',
    component: FileDownloadComponent,
  },
  {
    path: 'monitoring-approaches-prepare',
    data: { permitTask: 'monitoringApproachesPrepare' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Preparing to define monitoring approaches' },
        component: ApproachesPrepareComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Preparing to define monitoring approaches - Summary page',
          breadcrumb: 'Preparing to define monitoring approaches',
        },
        component: ApproachesPrepareSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'monitoring-approaches',
    data: { permitTask: 'monitoringApproaches' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Define monitoring approaches' },
        component: ApproachesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Define monitoring approaches', backlink: '../' },
        component: ApproachesAddComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Define monitoring approaches - Summary page', breadcrumb: 'Monitoring approaches' },
        component: ApproachesSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: 'help-with-monitoring-approaches',
        data: { pageTitle: 'Define monitoring approaches - Get help with monitoring approaches', backlink: '../' },
        component: ApproachesHelpComponent,
      },
      {
        path: 'help-with-source-stream-categories',
        data: { pageTitle: 'Define monitoring approaches - Get help with source stream categories', backlink: '../' },
        component: SourceStreamHelpComponent,
      },
    ],
  },
  {
    path: 'calculation',
    data: { permitTask: 'CALCULATION_CO2' },
    loadChildren: () => import('./approaches/calculation/calculation.module').then((m) => m.CalculationModule),
  },
  {
    path: 'nitrous-oxide',
    data: { permitTask: 'MEASUREMENT_N2O' },
    loadChildren: () => import('./approaches/n2o/n2o.module').then((m) => m.N2oModule),
  },
  {
    path: 'inherent-co2',
    data: { permitTask: 'INHERENT_CO2' },
    loadChildren: () => import('./approaches/inherent-co2/inherent-co2.module').then((m) => m.InherentCo2Module),
  },
  {
    path: 'transferred-co2',
    data: { permitTask: 'TRANSFERRED_CO2_N2O' },
    loadChildren: () =>
      import('./approaches/transferred-co2/transferred-co2.module').then((m) => m.TransferredCO2Module),
  },
  {
    path: 'fall-back',
    data: { permitTask: 'FALLBACK' },
    loadChildren: () => import('./approaches/fallback/fallback.module').then((m) => m.FallbackModule),
  },
  {
    path: 'measurement',
    data: { permitTask: 'MEASUREMENT_CO2' },
    loadChildren: () => import('./approaches/measurement/measurement.module').then((m) => m.MeasurementModule),
  },
  {
    path: 'pfc',
    data: { permitTask: 'CALCULATION_PFC' },
    loadChildren: () => import('./approaches/pfc/pfc.module').then((m) => m.PFCModule),
  },
  {
    path: 'uncertainty-analysis',
    data: { permitTask: 'uncertaintyAnalysis' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Uncertainty analysis' },
        component: UncertaintyAnalysisComponent,
        canActivate: [UncertaintyAnalysisGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-file',
        data: { pageTitle: 'Uncertainty analysis', backlink: '../' },
        component: UploadFileComponent,
        canActivate: [UploadFileGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Uncertainty analysis - Confirm answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Uncertainty analysis - Summary page', breadcrumb: 'Uncertainty analysis' },
        component: UncertaintyAnalysisSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'amend/:section',
    children: [
      {
        path: '',
        data: { pageTitle: 'Amend information' },
        component: AmendComponent,
        canActivate: [AmendGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Amend information - Summary page', breadcrumb: 'Amend information' },
        component: AmendSummaryComponent,
        canActivate: [AmendSummaryGuard],
      },
    ],
  },
  {
    path: 'tasks',
    loadChildren: () => import('../tasks/tasks.module').then((m) => m.TasksModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(permitRoutes)],
  exports: [RouterModule],
})
export class PermitApplicationRoutingModule {}
