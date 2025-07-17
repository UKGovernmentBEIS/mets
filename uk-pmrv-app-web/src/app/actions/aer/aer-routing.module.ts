import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { AerMarkAsNotRequiredDetailsComponent } from '@actions/aer/aer-mark-as-not-required/aer-mark-as-not-required-details/aer-mark-as-not-required-details.component';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { AerComponent } from './aer.component';
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
import { VerifierReturnedToOperatorActionComponent } from './verifier-returned-to-operator/verifier-returned-to-operator.component';

const routes: Route[] = [
  {
    path: '',
    component: AerComponent,
    data: { pageTitle: 'Emissions report' },
    children: [
      {
        path: 'submitted',
        data: { pageTitle: 'Emissions report' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Emissions report' },
            component: SubmittedComponent,
          },
          {
            path: 'installation-details',
            data: { pageTitle: 'Installation and operator details', breadcrumb: true },
            component: InstallationDetailsComponent,
          },
          {
            path: 'prtr',
            data: { pageTitle: 'Pollutant Release and Transfer Register codes (PRTR)', breadcrumb: true },
            component: PrtrComponent,
          },
          {
            path: 'nace-codes',
            data: { pageTitle: 'NACE codes', breadcrumb: true },
            component: NaceCodesComponent,
          },
          {
            path: 'regulated-activities',
            data: { pageTitle: 'Regulated activities carried out at the installation', breadcrumb: true },
            component: RegulatedActivitiesComponent,
          },
          {
            path: 'monitoring-plan',
            data: { pageTitle: 'Monitoring plan versions during the reporting year', breadcrumb: true },
            component: MonitoringPlanComponent,
          },
          {
            path: 'monitoring-approaches',
            data: { pageTitle: 'Monitoring approaches used during the reporting year', breadcrumb: true },
            component: MonitoringApproachesComponent,
          },
          {
            path: 'source-streams',
            data: { pageTitle: 'Source streams (fuels and materials)', breadcrumb: true },
            component: SourceStreamsComponent,
          },
          {
            path: 'emission-sources',
            data: { pageTitle: 'Emission sources', breadcrumb: true },
            component: EmissionSourcesComponent,
          },
          {
            path: 'emission-points',
            data: { pageTitle: 'Emission points', breadcrumb: true },
            component: EmissionPointsComponent,
          },
          {
            path: 'calculation-emissions',
            data: { breadcrumb: 'Calculation of CO2 emissions' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Calculation of CO2 emissions', taskKey: 'CALCULATION_CO2', breadcrumb: true },
                component: ApproachesComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Calculation emissions - Calculation approach tier',
                  taskKey: 'CALCULATION_CO2',
                  breadcrumb: 'Calculation approach tier',
                },
                component: ApproachesTierComponent,
              },
            ],
          },
          {
            path: 'pfc',
            data: { breadcrumb: 'Calculation of PFC' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Calculation of PFC', taskKey: 'CALCULATION_PFC', breadcrumb: true },
                component: ApproachesComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Calculation of PFC - Calculation approach tier',
                  taskKey: 'CALCULATION_PFC',
                  breadcrumb: 'Calculation approach tier',
                },
                component: ApproachesTierComponent,
              },
            ],
          },
          {
            path: 'measurement-co2',
            data: { breadcrumb: 'Measurement CO2 emissions' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Measurement CO2 emissions', taskKey: 'MEASUREMENT_CO2', breadcrumb: true },
                component: MeasurementComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Measurement CO2 - Measurement approach tier',
                  taskKey: 'MEASUREMENT_CO2',
                  breadcrumb: 'Measurement approach tier',
                },
                component: MeasurementTierComponent,
              },
            ],
          },
          {
            path: 'measurement-n2o',
            data: { breadcrumb: 'Measurement N2O emissions' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Measurement N2O emissions', taskKey: 'MEASUREMENT_N2O', breadcrumb: true },
                component: MeasurementComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Measurement N2O - Measurement approach tier',
                  taskKey: 'MEASUREMENT_N2O',
                  breadcrumb: 'Measurement approach tier',
                },
                component: MeasurementTierComponent,
              },
            ],
          },
          {
            path: 'fallback',
            data: { pageTitle: 'Monitoring approaches - Fallback', breadcrumb: true },
            component: FallbackComponent,
          },
          {
            path: 'inherent-co2-emissions',
            data: { pageTitle: 'Monitoring approaches - Inherent CO2', breadcrumb: true },
            component: InherentCo2Component,
          },
          {
            path: 'emissions-summary',
            data: { pageTitle: 'Emissions summary', breadcrumb: true },
            component: EmissionsSummaryComponent,
          },
          {
            path: 'abbreviations',
            data: { pageTitle: 'Abbreviations and definitions', breadcrumb: true },
            component: AbbreviationsComponent,
          },
          {
            path: 'additional-documents',
            data: { pageTitle: 'Additional documents and information', breadcrumb: true },
            component: AdditionalDocumentsComponent,
          },
          {
            path: 'confidentiality-statement',
            data: { pageTitle: 'Confidentiality statement', breadcrumb: true },
            component: ConfidentialityStatementComponent,
          },
          {
            path: 'activity-level-report',
            data: { pageTitle: 'Activity level report', breadcrumb: true },
            component: ActivityLevelReportComponent,
          },
          {
            path: 'details',
            data: { pageTitle: 'Installation details', breadcrumb: true },
            component: DetailsComponent,
          },
          {
            path: 'additional-info',
            data: { pageTitle: 'Additional information', breadcrumb: true },
            component: AdditionalInfoComponent,
          },
          {
            path: 'fuels',
            data: { pageTitle: 'Fuels and equipment inventory', breadcrumb: true },
            component: FuelsComponent,
          },
          {
            path: 'verifier-details',
            data: { pageTitle: 'Verifier details', breadcrumb: true },
            component: VerifierDetailsComponent,
          },
          {
            path: 'compliance-ets',
            data: { pageTitle: 'Compliance with ETS rules', breadcrumb: true },
            component: ComplianceEtsComponent,
          },
          {
            path: 'compliance-monitoring',
            data: { pageTitle: 'Compliance with monitoring and reporting principles', breadcrumb: true },
            component: ComplianceMonitoringComponent,
          },
          {
            path: 'verified-activity-level-report',
            data: { pageTitle: 'Verification report of the activity level report', breadcrumb: true },
            component: VerifiedActivityLevelReportComponent,
          },
          {
            path: 'overall-decision',
            data: { pageTitle: 'Overall decision', breadcrumb: true },
            component: OverallDecisionComponent,
          },
          {
            path: 'misstatements',
            data: { pageTitle: 'Uncorrected misstatements', breadcrumb: true },
            component: MisstatementsComponent,
          },
          {
            path: 'data-gaps',
            data: { pageTitle: 'Methodologies to close data gaps', breadcrumb: true },
            component: DataGapsComponent,
          },
          {
            path: 'materiality-level',
            data: { pageTitle: 'Materiality level and reference documents', breadcrumb: true },
            component: MaterialityLevelComponent,
          },
          {
            path: 'summary-of-conditions',
            data: { pageTitle: 'Summary of conditions, changes, clarifications and variations', breadcrumb: true },
            component: SummaryOfConditionsComponent,
          },
          {
            path: 'opinion-statement',
            data: { pageTitle: 'Opinion statement', breadcrumb: true },
            component: OpinionStatementComponent,
          },
          {
            path: 'non-conformities',
            data: { pageTitle: 'Uncorrected non-conformities', breadcrumb: true },
            component: NonConformitiesComponent,
          },
          {
            path: 'non-compliances',
            data: { pageTitle: 'Uncorrected non-compliances', breadcrumb: true },
            component: NonCompliancesComponent,
          },
          {
            path: 'recommended-improvements',
            data: { pageTitle: 'Recommended improvements', breadcrumb: true },
            component: RecommendedImprovementsComponent,
          },
        ],
      },
      {
        path: 'reviewed',
        children: [
          {
            path: '',
            data: { pageTitle: 'Emissions report' },
            component: ReviewedComponent,
          },
          {
            path: 'details',
            data: { pageTitle: 'Installation details', groupKey: 'INSTALLATION_DETAILS', breadcrumb: true },
            component: ReviewedDetailsComponent,
          },
          {
            path: 'fuels',
            data: { pageTitle: 'Fuels and equipment inventory', groupKey: 'FUELS_AND_EQUIPMENT', breadcrumb: true },
            component: ReviewedFuelsComponent,
          },
          {
            path: 'calculation-emissions',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Monitoring approaches - Calculation approach',
                  groupKey: 'CALCULATION_CO2',
                  breadcrumb: true,
                },
                component: ReviewedApproachesComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Calculation emissions - Calculation approach tier',
                  taskKey: 'CALCULATION_CO2',
                  breadcrumb: 'Calculation approach tier',
                },
                component: ApproachesTierComponent,
              },
            ],
          },
          {
            path: 'pfc',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Monitoring approaches - Calculation approach',
                  groupKey: 'CALCULATION_PFC',
                  breadcrumb: true,
                },
                component: ReviewedApproachesComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Calculation emissions - Calculation approach tier',
                  taskKey: 'CALCULATION_PFC',
                  breadcrumb: 'Calculation approach tier',
                },
                component: ApproachesTierComponent,
              },
            ],
          },
          {
            path: 'measurement-co2',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Monitoring approaches - Measurement CO2 approach',
                  groupKey: 'MEASUREMENT_CO2',
                  taskKey: 'MEASUREMENT_CO2',
                  breadcrumb: true,
                },
                component: ReviewedMeasurementComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Monitoring approaches - Measurement CO2 approach tier',
                  groupKey: 'MEASUREMENT_CO2',
                  taskKey: 'MEASUREMENT_CO2',
                  breadcrumb: 'Measurement CO2 approach tier',
                },
                component: MeasurementTierComponent,
              },
            ],
          },
          {
            path: 'measurement-n2o',
            children: [
              {
                path: '',
                data: {
                  pageTitle: 'Monitoring approaches - Measurement N2O approach',
                  groupKey: 'MEASUREMENT_N2O',
                  taskKey: 'MEASUREMENT_N2O',
                  breadcrumb: true,
                },
                component: ReviewedMeasurementComponent,
              },
              {
                path: ':index/summary',
                data: {
                  pageTitle: 'Monitoring approaches - Measurement N2O approach tier',
                  groupKey: 'MEASUREMENT_N2O',
                  taskKey: 'MEASUREMENT_N2O',
                  breadcrumb: true,
                },
                component: MeasurementTierComponent,
              },
            ],
          },
          {
            path: 'fallback',
            data: { pageTitle: 'Monitoring approaches - Fallback', groupKey: 'FALLBACK', breadcrumb: true },
            component: ReviewedFallbackComponent,
          },
          {
            path: 'inherent-co2-emissions',
            data: { pageTitle: 'Monitoring approaches - Inherent CO2', groupKey: 'INHERENT_CO2', breadcrumb: true },
            component: ReviewedInherentCo2Component,
          },
          {
            path: 'emissions-summary',
            data: { pageTitle: 'Emissions summary', groupKey: 'EMISSIONS_SUMMARY', breadcrumb: true },
            component: ReviewedEmissionsSummaryComponent,
          },
          {
            path: 'additional-info',
            data: { pageTitle: 'Additional information', groupKey: 'ADDITIONAL_INFORMATION', breadcrumb: true },
            component: ReviewedAdditionalInfoComponent,
          },
          {
            path: 'verifier-details',
            data: { pageTitle: 'Verifier details', groupKey: 'VERIFIER_DETAILS', breadcrumb: true },
            component: ReviewedVerifierDetailsComponent,
          },
          {
            path: 'compliance-ets',
            data: { pageTitle: 'Compliance with ETS rules', groupKey: 'ETS_COMPLIANCE_RULES', breadcrumb: true },
            component: ReviewedComplianceEtsComponent,
          },
          {
            path: 'compliance-monitoring',
            data: {
              pageTitle: 'Compliance with monitoring and reporting principles',
              groupKey: 'COMPLIANCE_MONITORING_REPORTING',
              breadcrumb: true,
            },
            component: ReviewedComplianceMonitoringComponent,
          },
          {
            path: 'activity-level-report',
            data: { pageTitle: 'Activity level report', groupKey: 'ACTIVITY_LEVEL_REPORT', breadcrumb: true },
            component: ReviewedActivityLevelReportComponent,
          },
          {
            path: 'overall-decision',
            data: { pageTitle: 'Overall decision', groupKey: 'OVERALL_DECISION', breadcrumb: true },
            component: ReviewedOverallDecisionComponent,
          },
          {
            path: 'misstatements',
            data: { pageTitle: 'Uncorrected misstatements', groupKey: 'UNCORRECTED_MISSTATEMENTS', breadcrumb: true },
            component: ReviewedMisstatementsComponent,
          },
          {
            path: 'data-gaps',
            data: {
              pageTitle: 'Methodologies to close data gaps',
              groupKey: 'CLOSE_DATA_GAPS_METHODOLOGIES',
              breadcrumb: true,
            },
            component: ReviewedDataGapsComponent,
          },
          {
            path: 'materiality-level',
            data: {
              pageTitle: 'Materiality level and reference documents',
              groupKey: 'MATERIALITY_LEVEL',
              breadcrumb: true,
            },
            component: ReviewedMaterialityLevelComponent,
          },
          {
            path: 'verified-activity-level-report',
            data: {
              pageTitle: 'Verification report of the activity level report',
              groupKey: 'VERIFICATION_ACTIVITY_LEVEL_REPORT',
              breadcrumb: true,
            },
            component: ReviewedVerifiedActivityLevelReportComponent,
          },
          {
            path: 'summary-of-conditions',
            data: {
              pageTitle: 'Summary of conditions, changes, clarifications and variations',
              groupKey: 'SUMMARY_OF_CONDITIONS',
              breadcrumb: true,
            },
            component: ReviewedSummaryOfConditionsComponent,
          },
          {
            path: 'opinion-statement',
            data: {
              pageTitle: 'Opinion statement',
              groupKey: 'OPINION_STATEMENT',
              breadcrumb: true,
            },
            component: ReviewedOpinionStatementComponent,
          },
          {
            path: 'non-conformities',
            data: {
              pageTitle: 'Uncorrected non-conformities',
              groupKey: 'UNCORRECTED_NON_CONFORMITIES',
              breadcrumb: true,
            },
            component: ReviewedNonConformitiesComponent,
          },
          {
            path: 'non-compliances',
            data: {
              pageTitle: 'Uncorrected non-compliances',
              groupKey: 'UNCORRECTED_NON_COMPLIANCES',
              breadcrumb: true,
            },
            component: ReviewedNonCompliancesComponent,
          },
          {
            path: 'recommended-improvements',
            data: { pageTitle: 'Recommended improvements', groupKey: 'RECOMMENDED_IMPROVEMENTS', breadcrumb: true },
            component: ReviewedRecommendedImprovementsComponent,
          },
        ],
      },
      {
        path: 'return-for-amends',
        data: { pageTitle: 'Emissions report' },
        component: ReturnForAmendsComponent,
      },
      {
        path: 'completed',
        data: { pageTitle: 'Emissions report' },
        component: CompletedComponent,
      },
      {
        path: 'decision/details',
        data: {
          pageTitle: 'Marked as not required',
          breadcrumb: 'Marked as not required',
        },
        component: AerMarkAsNotRequiredDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'returned-to-operator',
        data: { pageTitle: 'Emissions report' },
        component: VerifierReturnedToOperatorActionComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AerRoutingModule {}
