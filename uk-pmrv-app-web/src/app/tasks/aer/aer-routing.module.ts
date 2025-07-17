import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { ActivityLevelReportComponent as ReviewActivityLevelReportComponent } from '@tasks/aer/review/activity-level-report/activity-level-report.component';
import { AdditionalInfoComponent as ReviewAdditionalInfoComponent } from '@tasks/aer/review/additional-info/additional-info.component';
import { ApproachesComponent as ReviewApproachesComponent } from '@tasks/aer/review/approaches/approaches.component';
import { CompleteReviewComponent } from '@tasks/aer/review/complete-review/complete-review.component';
import { CompleteReviewGuard } from '@tasks/aer/review/complete-review/complete-review.guard';
import { ComplianceEtsComponent } from '@tasks/aer/review/compliance-ets/compliance-ets.component';
import { ComplianceMonitoringComponent as ReviewComplianceMonitoringComponent } from '@tasks/aer/review/compliance-monitoring/compliance-monitoring.component';
import { DataGapsComponent as ReviewDataGapsComponent } from '@tasks/aer/review/data-gaps/data-gaps.component';
import { DetailsComponent as ReviewDetailsComponent } from '@tasks/aer/review/details/details.component';
import { FallbackComponent as ReviewFallbackComponent } from '@tasks/aer/review/fallback/fallback.component';
import { FuelsComponent as ReviewFuelsComponent } from '@tasks/aer/review/fuels/fuels.component';
import { InherentCo2Component as ReviewInherentCo2Component } from '@tasks/aer/review/inherent-co2/inherent-co2.component';
import { MaterialityLevelComponent as ReviewMaterialityLevelComponent } from '@tasks/aer/review/materiality-level/materiality-level.component';
import { MeasurementReviewComponent } from '@tasks/aer/review/measurement/measurement-review.component';
import { MisstatementsComponent as ReviewMisstatementsComponent } from '@tasks/aer/review/misstatements/misstatements.component';
import { NonCompliancesComponent } from '@tasks/aer/review/non-compliances/non-compliances.component';
import { NonConformitiesComponent } from '@tasks/aer/review/non-conformities/non-conformities.component';
import { OpinionStatementComponent as ReviewOpinionStatementComponent } from '@tasks/aer/review/opinion-statement/opinion-statement.component';
import { OverallDecisionComponent as ReviewOverallDecisionComponent } from '@tasks/aer/review/overall-decision/overall-decision.component';
import { RecommendedImprovementsComponent } from '@tasks/aer/review/recommended-improvements/recommended-improvements.component';
import { ReviewContainerComponent } from '@tasks/aer/review/review-container.component';
import { SummaryOfConditionsComponent as ReviewSummaryOfConditionsComponent } from '@tasks/aer/review/summary-of-conditions/summary-of-conditions.component';
import { VerifiedActivityLevelReportComponent as ReviewVerifiedActivityLevelReportComponent } from '@tasks/aer/review/verified-activity-level-report/verified-activity-level-report.component';
import { VerifierDetailsComponent as ReviewVerifierDetailsComponent } from '@tasks/aer/review/verifier-details/verifier-details.component';
import { ReviewWaitComponent } from '@tasks/aer/review-wait/review-wait.component';
import { ApproachesTierReviewSummaryComponent } from '@tasks/aer/shared/components/approaches-tier-review-summary/approaches-tier-review-summary.component';
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
import { EmissionPointDetailsGuard } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.guard';
import { EmissionPointsComponent } from '@tasks/aer/submit/emission-points/emission-points.component';
import { EmissionSourceDeleteComponent } from '@tasks/aer/submit/emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourceDetailsGuard } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.guard';
import { EmissionSourcesComponent } from '@tasks/aer/submit/emission-sources/emission-sources.component';
import { MonitoringPlanComponent } from '@tasks/aer/submit/monitoring-plan/monitoring-plan.component';
import { SummaryComponent as SummaryMonitoringPlanComponent } from '@tasks/aer/submit/monitoring-plan/summary/summary.component';
import { NaceCodeDeleteComponent } from '@tasks/aer/submit/nace-codes/nace-code-delete/nace-code-delete.component';
import { NaceCodesComponent } from '@tasks/aer/submit/nace-codes/nace-codes.component';
import { NaceCodeInstallationActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.component';
import { NaceCodeInstallationActivityGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.guard';
import { NaceCodeMainActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity.component';
import { NaceCodeSubCategoryComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.component';
import { NaceCodeSubCategoryGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.guard';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { ActivityGuard } from '@tasks/aer/submit/prtr/activity/activity.guard';
import { DeleteComponent as PrtrDeleteComponent } from '@tasks/aer/submit/prtr/activity/delete/delete.component';
import { PrtrComponent } from '@tasks/aer/submit/prtr/prtr.component';
import { SummaryComponent as PrtrSummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { SummaryGuard as PrtrSummaryGuard } from '@tasks/aer/submit/prtr/summary/summary.guard';
import { CapacityComponent } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity.component';
import { CrfCodesComponent } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes.component';
import { EnergyCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code.component';
import { IndustrialCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code.component';
import { RegulatedActivityComponent } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.component';
import { RegulatedActivityGuard } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.guard';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/submit/regulated-activities/delete/regulated-activity-delete.component';
import { RegulatedActivitiesComponent } from '@tasks/aer/submit/regulated-activities/regulated-activities.component';
import { RegulatorComponent } from '@tasks/aer/submit/send-report/regulator/regulator.component';
import { SendReportComponent } from '@tasks/aer/submit/send-report/send-report.component';
import { SendReportGuard } from '@tasks/aer/submit/send-report/send-report.guard';
import { VerificationComponent } from '@tasks/aer/submit/send-report/verification/verification.component';
import { VerificationGuard } from '@tasks/aer/submit/send-report/verification/verification.guard';
import { SourceStreamDeleteComponent } from '@tasks/aer/submit/source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsGuard } from '@tasks/aer/submit/source-streams/source-stream-details/source-stream-details.guard';
import { SourceStreamDetailsComponent } from '@tasks/aer/submit/source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from '@tasks/aer/submit/source-streams/source-streams.component';
import { SummaryGuard } from '@tasks/aer/submit/summary.guard';
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
import { FallbackComponent } from '@tasks/aer/verification-submit/fallback/fallback.component';
import { InherentCo2Component } from '@tasks/aer/verification-submit/inherent-co2/inherent-co2.component';
import { MaterialityLevelComponent } from '@tasks/aer/verification-submit/materiality-level/materiality-level.component';
import { ReferenceDocumentsComponent } from '@tasks/aer/verification-submit/materiality-level/reference-documents/reference-documents.component';
import { SummaryComponent as MaterialityLevelSummary } from '@tasks/aer/verification-submit/materiality-level/summary/summary.component';
import { NotVerifiedComponent } from '@tasks/aer/verification-submit/overall-decision/not-verified/not-verified.component';
import { OverallDecisionComponent } from '@tasks/aer/verification-submit/overall-decision/overall-decision.component';
import { ReasonItemDeleteComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/delete/reason-item-delete.component';
import { ReasonItemComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-item.component';
import { ReasonItemGuard } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-item.guard';
import { ReasonListComponent } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-list.component';
import { ReasonListGuard } from '@tasks/aer/verification-submit/overall-decision/reason-list/reason-list.guard';
import { SummaryComponent as OverallDecisionSummaryComponent } from '@tasks/aer/verification-submit/overall-decision/summary/summary.component';
import { SendReportComponent as VerifierSendReportComponent } from '@tasks/aer/verification-submit/send-report/send-report.component';
import { SummaryGuard as VerificationSubmitSummaryGuard } from '@tasks/aer/verification-submit/summary.guard';
import { IdentifiedChangesComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes/identified-changes.component';
import { IdentifiedChangesDeleteComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/delete/identified-changes-delete.component';
import { IdentifiedChangesItemComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-item.component';
import { IdentifiedChangesItemGuard } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-item.guard';
import { IdentifiedChangesListComponent } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-list.component';
import { IdentifiedChangesListGuard } from '@tasks/aer/verification-submit/summary-of-conditions/identified-changes-list/identified-changes-list.guard';
import { NotIncludedDeleteComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/delete/not-included-delete.component';
import { NotIncludedItemComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-item.component';
import { NotIncludedItemGuard } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-item.guard';
import { NotIncludedListComponent } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-list.component';
import { NotIncludedListGuard } from '@tasks/aer/verification-submit/summary-of-conditions/not-included-list/not-included-list.guard';
import { SummaryComponent as SummaryOfConditionsSummaryComponent } from '@tasks/aer/verification-submit/summary-of-conditions/summary/summary.component';
import { SummaryOfConditionsComponent } from '@tasks/aer/verification-submit/summary-of-conditions/summary-of-conditions.component';
import { VerificationSubmitContainerComponent } from '@tasks/aer/verification-submit/verification-submit-container.component';
import { SummaryComponent as VerifierDetailsSummaryComponent } from '@tasks/aer/verification-submit/verifier-details/summary/summary.component';
import { VerifierDetailsComponent } from '@tasks/aer/verification-submit/verifier-details/verifier-details.component';
import { RecallComponent } from '@tasks/aer/verification-wait/recall/recall.component';
import { VerificationWaitComponent } from '@tasks/aer/verification-wait/verification-wait.component';

import { EmissionsSummaryComponent as ReviewEmissionsSummaryComponent } from './review/emissions-summary/emissions-summary.component';
import { ReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { SkipReviewComponent } from './review/skip-review/skip-review.component';
import { canSkipReview } from './review/skip-review/skip-review.guard';
import { MeasurementTierReviewSummaryComponent } from './shared/components/measurement-tier-review-summary/measurement-tier-review-summary.component';
import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { AmendComponent } from './submit/amend/amend.component';
import { AmendSummaryComponent } from './submit/amend/summary/amend-summary.component';
import { AmendSummaryGuard } from './submit/amend/summary/amend-summary.guard';
import { EmissionsSummaryComponent } from './submit/emissions-summary/emissions-summary.component';
import { InstallationDetailsComponent } from './submit/installation-details/installation-details.component';
import { RefreshResolver } from './submit/prtr/activity/refresh.resolver';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { ActivityLevelReportComponent } from './verification-submit/activity-level-report/activity-level-report.component';
import { FuelsComponent } from './verification-submit/fuels/fuels.component';
import { MeasurementVerificationComponent } from './verification-submit/measurement/measurement-verification.component';
import { PfcVerificationComponent } from './verification-submit/pfc/pfc-verification.component';
import { AerVerifyReturnToOperatorForChangesComponent } from './verification-submit/return-to-operator-for-changes/return-to-operator-for-changes.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions report' },
        component: SubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'installation-details',
        data: { pageTitle: 'Installation and operator details', breadcrumb: true },
        component: InstallationDetailsComponent,
      },
      {
        path: 'prtr',
        data: { aerTask: 'pollutantRegisterActivities' },
        children: [
          {
            path: '',
            data: { pageTitle: 'European Pollutant Release and Transfer Register codes (PRTR)' },
            component: PrtrComponent,
          },
          {
            path: 'activity/:index',
            children: [
              {
                path: '',
                data: { pageTitle: 'European Pollutant Release and Transfer Register codes (PRTR) - New entry' },
                component: ActivityComponent,
                canActivate: [ActivityGuard],
                resolve: { refresh: RefreshResolver },
              },
              {
                path: 'delete',
                data: { pageTitle: 'Are you sure you want to delete this activity?' },
                component: PrtrDeleteComponent,
                canActivate: [ActivityGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'PRTR - Summary page',
              breadcrumb: 'European Pollutant Release and Transfer Register codes (PRTR)',
            },
            component: PrtrSummaryComponent,
            canDeactivate: [PendingRequestGuard],
            canActivate: [PrtrSummaryGuard],
          },
        ],
      },
      {
        path: 'abbreviations',
        data: { aerTask: 'abbreviations' },
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
              pageTitle: 'Definitions of abbreviations, acronyms and terminology - Check your answers',
              breadcrumb: 'Abbreviations',
            },
            component: SummaryAbbreviationsComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'source-streams',
        data: { aerTask: 'source-streams', breadcrumb: 'Source streams (fuels and materials)' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Source streams (fuels and materials)' },
            component: SourceStreamsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add a source stream' },
            component: SourceStreamDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:streamId',
            data: { pageTitle: 'Are you sure you want to delete this source stream?' },
            component: SourceStreamDeleteComponent,
            canActivate: [SourceStreamDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':streamId',
            data: { pageTitle: 'Edit source stream' },
            component: SourceStreamDetailsComponent,
            canActivate: [SourceStreamDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'monitoring-approaches',
        data: { permitTask: 'monitoringApproachEmissions', breadcrumb: 'Define monitoring approaches' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Define monitoring approaches' },
            component: ApproachesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Define monitoring approaches' },
            component: ApproachesAddComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:monitoringApproach',
            data: { pageTitle: 'Are you sure you want to delete this monitoring approach?' },
            component: ApproachesDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'help-with-monitoring-approaches',
            data: { pageTitle: 'Define monitoring approaches - Get help with monitoring approaches' },
            component: ApproachesHelpComponent,
          },
          {
            path: 'help-with-source-stream-categories',
            data: { pageTitle: 'Define monitoring approaches - Get help with source stream categories' },
            component: SourceStreamHelpComponent,
          },
        ],
      },
      {
        path: 'emission-points',
        data: { aerTask: 'emission-points', breadcrumb: 'Emission points' },
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
            path: ':emissionPointId',
            data: { pageTitle: 'Edit emission point', backlink: '../' },
            component: EmissionPointDetailsComponent,
            canActivate: [EmissionPointDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'confidentiality-statement',
        data: { aerTask: 'confidentialityStatement' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Confidentiality statement' },
            component: ConfidentialityStatementComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Confidentiality statement - Check your answers',
              breadcrumb: 'Confidentiality statement',
            },
            component: SummaryConfidentialityStatementComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'activity-level-report',
        data: { aerTask: 'activityLevelReport' },
        loadChildren: () =>
          import('./submit/activity-level-report/activity-level-report.module').then(
            (m) => m.ActivityLevelReportModule,
          ),
      },
      {
        path: 'emission-sources',
        data: { permitTask: 'emissionSources', breadcrumb: 'Emission sources' },
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
            path: ':sourceId',
            data: { pageTitle: 'Edit emission source', backlink: '../' },
            component: EmissionSourceDetailsComponent,
            canActivate: [EmissionSourceDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'nace-codes',
        data: { aerTask: 'naceCodes', breadcrumb: 'NACE codes' },
        children: [
          {
            path: '',
            data: { pageTitle: 'NACE codes' },
            component: NaceCodesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add a NACE code' },
            children: [
              {
                path: '',
                data: { backlink: '../' },
                component: NaceCodeMainActivityComponent,
              },
              {
                path: 'sub-category',
                data: { backlink: '../' },
                component: NaceCodeSubCategoryComponent,
                canActivate: [NaceCodeSubCategoryGuard],
              },
              {
                path: 'installation-activity',
                data: { backlink: '../sub-category' },
                component: NaceCodeInstallationActivityComponent,
                canActivate: [NaceCodeInstallationActivityGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'delete/:naceCode',
            data: { pageTitle: 'Are you sure you want to delete this NACE code?', backlink: '../' },
            component: NaceCodeDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'additional-documents',
        data: { aerTask: 'additionalDocuments' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Additional documents and information' },
            component: AdditionalDocumentsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Additional documents and information - Check your answers',
              breadcrumb: 'Additional documents',
            },
            component: SummaryAdditionalDocumentsComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'regulated-activities',
        data: { permitTask: 'regulatedActivities', breadcrumb: 'Regulated activities' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Regulated activities' },
            component: RegulatedActivitiesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add regulated activity', backlink: '../' },
            component: RegulatedActivityComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId',
            data: { pageTitle: 'Add regulated activity', backlink: '../' },
            component: RegulatedActivityComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/capacity',
            data: { pageTitle: 'Add regulated activity capacity', backlink: '../' },
            component: CapacityComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/crf-codes',
            data: { pageTitle: 'Add regulated activity crf codes', backlink: '../capacity' },
            component: CrfCodesComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/energy-crf-code',
            data: { pageTitle: 'Add regulated activity energy crf code', backlink: '../crf-codes' },
            component: EnergyCrfCodeComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/industrial-crf-code',
            data: { pageTitle: 'Add regulated activity industrial crf code', backlink: '../crf-codes' },
            component: IndustrialCrfCodeComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:activityId',
            data: { pageTitle: 'Are you sure you want to delete this regulated activity?', backlink: '../..' },
            component: RegulatedActivityDeleteComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'monitoring-plan',
        data: { aerTask: 'aerMonitoringPlanDeviation' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Monitoring plan changes' },
            component: MonitoringPlanComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Monitoring plan changes - Check your answers',
              breadcrumb: 'Monitoring plan changes',
            },
            component: SummaryMonitoringPlanComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'send-report',
        children: [
          {
            path: '',
            data: { pageTitle: 'Send report' },
            canActivate: [SendReportGuard],
            component: SendReportComponent,
          },
          {
            path: 'verification',
            data: { pageTitle: 'Send report for verification', backlink: '../..' },
            component: VerificationComponent,
            canActivate: [VerificationGuard],
          },
          {
            path: 'regulator',
            data: { pageTitle: 'Send report to regulator', backlink: '../..' },
            component: RegulatorComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'emissions-summary',
        children: [
          {
            path: '',
            data: { pageTitle: 'Emissions summary', breadcrumb: true },
            component: EmissionsSummaryComponent,
          },
        ],
      },
      {
        path: 'calculation-emissions',
        data: { breadcrumb: 'Calculation approach' },
        loadChildren: () =>
          import('./submit/calculation-emissions/calculation-emissions.module').then(
            (m) => m.CalculationEmissionsModule,
          ),
      },
      {
        path: 'measurement-co2',
        loadChildren: () => import('./submit/measurement/measurement.module').then((m) => m.MeasurementModule),
        data: { taskKey: 'MEASUREMENT_CO2' },
      },
      {
        path: 'measurement-n2o',
        loadChildren: () => import('./submit/measurement/measurement.module').then((m) => m.MeasurementModule),
        data: { taskKey: 'MEASUREMENT_N2O' },
      },
      {
        path: 'pfc',
        loadChildren: () => import('./submit/pfc/pfc.module').then((m) => m.PfcModule),
        data: { taskKey: 'PFC' },
      },
      {
        path: 'inherent-co2-emissions',
        data: { aerTask: 'INHERENT_CO2' },
        loadChildren: () => import('./submit/inherent-co2/inherent-co2.module').then((m) => m.InherentCo2Module),
      },
      {
        path: 'fallback',
        data: { aerTask: 'FALLBACK' },
        loadChildren: () => import('./submit/fallback/fallback.module').then((m) => m.FallbackModule),
      },
      {
        path: 'amend/:section',
        children: [
          {
            path: '',
            resolve: { section: (route) => route.paramMap.get('section') },
            data: { pageTitle: 'Amend information', breadcrumb: ({ section }) => `Amend ${section}` },
            component: AmendComponent,
            // canActivate: [AmendGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Amend information - Summary page', breadcrumb: 'Amend summary' },
            component: AmendSummaryComponent,
            canActivate: [AmendSummaryGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'verification-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions report' },
        component: VerificationWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'recall',
        data: { pageTitle: 'Recall AER application from verifier' },
        component: RecallComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'review-wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions report' },
        component: ReviewWaitComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
    ],
  },

  {
    path: 'verification-submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions report' },
        component: VerificationSubmitContainerComponent,
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'return-to-operator-for-changes',
        data: { pageTitle: 'Return to operator for changes' },
        component: AerVerifyReturnToOperatorForChangesComponent,
      },
      {
        path: 'details',
        data: { pageTitle: 'Installation details' },
        component: DetailsComponent,
      },
      {
        path: 'fuels',
        data: { pageTitle: 'Fuels and equipment inventory' },
        component: FuelsComponent,
      },
      {
        path: 'calculation-emissions',
        children: [
          {
            path: '',
            data: { pageTitle: 'Monitoring approaches - Calculation approach' },
            component: CalculationEmissionsComponent,
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - Calculation approach tier',
              groupKey: 'CALCULATION_CO2',
            },
            component: ApproachesTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'pfc',
        children: [
          {
            path: '',
            data: { pageTitle: 'Monitoring approaches - PFC' },
            component: PfcVerificationComponent,
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - PFC tier',
              groupKey: 'CALCULATION_PFC',
            },
            component: ApproachesTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'measurement-co2',
        children: [
          {
            path: '',
            data: { pageTitle: 'Monitoring approaches - Measurement CO2', taskKey: 'MEASUREMENT_CO2' },
            component: MeasurementVerificationComponent,
          },
          {
            path: ':index/summary',
            data: { pageTitle: 'Monitoring approaches - Measurement CO2 tier', taskKey: 'MEASUREMENT_CO2' },
            component: MeasurementTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'measurement-n2o',
        children: [
          {
            path: '',
            data: { pageTitle: 'Monitoring approaches - Measurement N2O', taskKey: 'MEASUREMENT_N2O' },
            component: MeasurementVerificationComponent,
          },
          {
            path: ':index/summary',
            data: { pageTitle: 'Monitoring approaches - Measurement N2O tier', taskKey: 'MEASUREMENT_N2O' },
            component: MeasurementTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'fallback',
        data: { pageTitle: 'Monitoring approaches - Fallback' },
        component: FallbackComponent,
      },
      {
        path: 'inherent-co2-emissions',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2' },
        component: InherentCo2Component,
      },
      {
        path: 'emissions-summary',
        data: { pageTitle: 'Emissions summary' },
        component: VerifierEmissionsSummary,
      },
      {
        path: 'additional-info',
        data: { pageTitle: 'Additional information' },
        component: AdditionalInfoComponent,
      },
      {
        path: 'activity-level-report',
        data: { pageTitle: 'Activity level report' },
        component: ActivityLevelReportComponent,
      },
      {
        path: 'verifier-details',
        data: { aerTask: 'verificationTeamDetails' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Verifier details' },
            component: VerifierDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Verifier details summary' },
            component: VerifierDetailsSummaryComponent,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'materiality-level',
        data: { aerTask: 'materialityLevel' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Materiality level' },
            component: MaterialityLevelComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'reference-documents',
            data: { pageTitle: 'Select the reference documents', backlink: '../' },
            component: ReferenceDocumentsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Materiality level summary' },
            component: MaterialityLevelSummary,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'compliance-monitoring',
        data: { aerTask: 'complianceMonitoringReporting' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Compliance with the Monitoring and Reporting Principles' },
            component: ComplianceMonitoringComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Check your answers',
              breadcrumb: 'Compliance with the Monitoring and Reporting Principles summary',
            },
            component: ComplianceMonitoringSummaryComponent,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'verified-activity-level-report',
        data: { aerTask: 'activityLevelReport' },
        loadChildren: () =>
          import('./verification-submit/verified-activity-level-report/verified-activity-level-report.module').then(
            (m) => m.VerifiedActivityLevelReportModule,
          ),
      },
      {
        path: 'overall-decision',
        data: { aerTask: 'overallAssessment' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Overall assessment of the report' },
            component: OverallDecisionComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'not-verified',
            data: { pageTitle: 'Why can you not verify the report', backlink: '../' },
            component: NotVerifiedComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'reason-list',
            children: [
              {
                path: '',
                data: { pageTitle: 'List the reasons for your decision', backlink: '../' },
                component: ReasonListComponent,
                canActivate: [ReasonListGuard],
              },
              {
                path: ':index',
                data: { pageTitle: 'Add a reason', backlink: '../' },
                component: ReasonItemComponent,
                canActivate: [ReasonItemGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index/delete',
                data: { pageTitle: 'Are you sure you want to delete this item', backlink: '../..' },
                component: ReasonItemDeleteComponent,
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Overall decision summary' },
            component: OverallDecisionSummaryComponent,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'misstatements',
        data: { aerTask: 'uncorrectedMisstatements' },
        loadChildren: () =>
          import('./verification-submit/misstatements/misstatements.module').then((m) => m.MisstatementsModule),
      },
      {
        path: 'data-gaps',
        data: { aerTask: 'methodologiesToCloseDataGaps' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Was a data gap method required during the reporting year' },
            component: DataGapsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'regulator-approved',
            data: { pageTitle: 'Has the data gap method already been approved by the regulator', backlink: '../' },
            component: RegulatorApprovedComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'conservative-method',
            data: { pageTitle: 'Was the method used conservative', backlink: '../regulator-approved' },
            component: ConservativeMethodComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'material-misstatement',
            data: { pageTitle: 'Did the method lead to a material misstatement', backlink: '../conservative-method' },
            component: MaterialMisstatementComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Data gaps summary' },
            component: SummaryDataGapsComponent,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'summary-of-conditions',
        data: { aerTask: 'summaryOfConditions' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Changes approved by a regulator' },
            component: SummaryOfConditionsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'not-included-list',
            children: [
              {
                path: '',
                data: { pageTitle: 'Approved changes not included in a re-issued permit', backlink: '../' },
                component: NotIncludedListComponent,
                canActivate: [NotIncludedListGuard],
              },
              {
                path: ':index',
                data: { pageTitle: 'Add an approved change not included in a re-issued permit', backlink: '../' },
                component: NotIncludedItemComponent,
                canActivate: [NotIncludedItemGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index/delete',
                data: { pageTitle: 'Are you sure you want to delete this item', backlink: '../..' },
                component: NotIncludedDeleteComponent,
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'identified-changes',
            data: {
              pageTitle: 'Changes identified during your review that were not reported',
              backlink: '../not-included-list',
            },
            component: IdentifiedChangesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'identified-changes-list',
            children: [
              {
                path: '',
                data: { pageTitle: 'Changes not reported to the regulator', backlink: '../identified-changes' },
                component: IdentifiedChangesListComponent,
                canActivate: [IdentifiedChangesListGuard],
              },
              {
                path: ':index',
                data: { pageTitle: 'Add a change not reported to the regulator', backlink: '../' },
                component: IdentifiedChangesItemComponent,
                canActivate: [IdentifiedChangesItemGuard],
                canDeactivate: [PendingRequestGuard],
              },
              {
                path: ':index/delete',
                data: { pageTitle: 'Are you sure you want to delete this item', backlink: '../..' },
                component: IdentifiedChangesDeleteComponent,
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Check your answers', breadcrumb: 'Summary of conditions summary' },
            component: SummaryOfConditionsSummaryComponent,
            canActivate: [VerificationSubmitSummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'compliance-ets',
        data: { aerTask: 'etsComplianceRules' },
        loadChildren: () =>
          import('./verification-submit/compliance-ets/compliance-ets.module').then((m) => m.ComplianceEtsModule),
      },
      {
        path: 'opinion-statement',
        data: { aerTask: 'opinionStatement' },
        loadChildren: () =>
          import('./verification-submit/opinion-statement/opinion-statement.module').then(
            (m) => m.OpinionStatementModule,
          ),
      },
      {
        path: 'non-conformities',
        data: { aerTask: 'uncorrectedNonConformities' },
        loadChildren: () =>
          import('./verification-submit/non-conformities/non-conformities.module').then((m) => m.NonConformitiesModule),
      },
      {
        path: 'non-compliances',
        data: { aerTask: 'uncorrectedNonCompliances' },
        loadChildren: () =>
          import('./verification-submit/non-compliances/non-compliances.module').then((m) => m.NonCompliancesModule),
      },
      {
        path: 'recommended-improvements',
        data: { aerTask: 'recommendedImprovements' },
        loadChildren: () =>
          import('./verification-submit/recommended-improvements/recommended-improvements.module').then(
            (m) => m.RecommendedImprovementsModule,
          ),
      },
      {
        path: 'send-report',
        data: { pageTitle: 'Send report to operator' },
        component: VerifierSendReportComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        data: { pageTitle: 'Review emissions report' },
        component: ReviewContainerComponent,
      },
      {
        path: 'skip-review',
        children: [
          {
            path: '',
            component: SkipReviewComponent,
            data: { pageTitle: 'Skip review', breadcrumb: true },
            canActivate: [canSkipReview],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'change-assignee',
        loadChildren: () =>
          import('../../change-task-assignee/change-task-assignee.module').then((m) => m.ChangeTaskAssigneeModule),
      },
      {
        path: 'details',
        data: { pageTitle: 'Installation details', groupKey: 'INSTALLATION_DETAILS', breadcrumb: true },
        component: ReviewDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'fuels',
        data: { pageTitle: 'Fuels and equipment inventory', groupKey: 'FUELS_AND_EQUIPMENT', breadcrumb: true },
        component: ReviewFuelsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'calculation-emissions',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Calculation approach',
              groupKey: 'CALCULATION_CO2',
              breadcrumb: 'Calculation approach',
            },
            component: ReviewApproachesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - Calculation approach tier',
              groupKey: 'CALCULATION_CO2',
              breadcrumb: 'Calculation approach tier',
            },
            component: ApproachesTierReviewSummaryComponent,
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
              breadcrumb: 'Calculation approach',
            },
            component: ReviewApproachesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - Calculation approach tier',
              groupKey: 'CALCULATION_PFC',
              breadcrumb: 'Calculation approach tier',
            },
            component: ApproachesTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'measurement-co2',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Measurement CO2',
              groupKey: 'MEASUREMENT_CO2',
              taskKey: 'MEASUREMENT_CO2',
              breadcrumb: 'Measurement CO2',
            },
            component: MeasurementReviewComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - Measurement CO2 tier',
              groupKey: 'MEASUREMENT_CO2',
              taskKey: 'MEASUREMENT_CO2',
              breadcrumb: 'Measurement CO2 tier',
            },
            component: MeasurementTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'measurement-n2o',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Measurement N2O',
              groupKey: 'MEASUREMENT_N2O',
              taskKey: 'MEASUREMENT_N2O',
              breadcrumb: 'Measurement N2O',
            },
            component: MeasurementReviewComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':index/summary',
            data: {
              pageTitle: 'Monitoring approaches - Measurement N2O tier ',
              groupKey: 'MEASUREMENT_N2O',
              taskKey: 'MEASUREMENT_N2O',
              breadcrumb: 'Measurement N2O tier',
            },
            component: MeasurementTierReviewSummaryComponent,
          },
        ],
      },
      {
        path: 'fallback',
        data: { pageTitle: 'Monitoring approaches - Fallback', groupKey: 'FALLBACK', breadcrumb: 'Fallback' },
        component: ReviewFallbackComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'inherent-co2-emissions',
        data: {
          pageTitle: 'Monitoring approaches - Inherent CO2',
          groupKey: 'INHERENT_CO2',
          breadcrumb: 'Inherent CO2',
        },
        component: ReviewInherentCo2Component,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'emissions-summary',
        data: { pageTitle: 'Emissions summary', groupKey: 'EMISSIONS_SUMMARY', breadcrumb: true },
        component: ReviewEmissionsSummaryComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'activity-level-report',
        data: { pageTitle: 'Activity level report', groupKey: 'ACTIVITY_LEVEL_REPORT', breadcrumb: true },
        component: ReviewActivityLevelReportComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'additional-info',
        data: { pageTitle: 'Additional information', groupKey: 'ADDITIONAL_INFORMATION', breadcrumb: true },
        component: ReviewAdditionalInfoComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'verifier-details',
        data: { pageTitle: 'Verifier details', groupKey: 'VERIFIER_DETAILS', breadcrumb: true },
        component: ReviewVerifierDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'compliance-monitoring',
        data: {
          pageTitle: 'Compliance with monitoring and reporting principles',
          groupKey: 'COMPLIANCE_MONITORING_REPORTING',
          breadcrumb: true,
        },
        component: ReviewComplianceMonitoringComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'verified-activity-level-report',
        data: {
          pageTitle: 'Verification report of the activity level report',
          groupKey: 'VERIFICATION_ACTIVITY_LEVEL_REPORT',
          breadcrumb: true,
        },
        component: ReviewVerifiedActivityLevelReportComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'materiality-level',
        data: {
          pageTitle: 'Materiality level and reference documents',
          groupKey: 'MATERIALITY_LEVEL',
          breadcrumb: true,
        },
        component: ReviewMaterialityLevelComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'compliance-ets',
        data: { pageTitle: 'Compliance with ETS rules', groupKey: 'ETS_COMPLIANCE_RULES', breadcrumb: true },
        component: ComplianceEtsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'overall-decision',
        data: { pageTitle: 'Overall decision', groupKey: 'OVERALL_DECISION', breadcrumb: true },
        component: ReviewOverallDecisionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'misstatements',
        data: { pageTitle: 'Uncorrected misstatements', groupKey: 'UNCORRECTED_MISSTATEMENTS', breadcrumb: true },
        component: ReviewMisstatementsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'data-gaps',
        data: {
          pageTitle: 'Methodologies to close data gaps',
          groupKey: 'CLOSE_DATA_GAPS_METHODOLOGIES',
          breadcrumb: true,
        },
        component: ReviewDataGapsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary-of-conditions',
        data: { pageTitle: 'Summary of conditions', groupKey: 'SUMMARY_OF_CONDITIONS', breadcrumb: true },
        component: ReviewSummaryOfConditionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'complete-review',
        data: { pageTitle: 'Complete review', breadcrumb: true },
        component: CompleteReviewComponent,
        canActivate: [CompleteReviewGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amends', breadcrumb: true },
            component: ReturnForAmendsComponent,
            // canActivate: [CompleteReviewGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            data: { pageTitle: 'Return for amends confirmation' },
            component: CompleteReviewComponent,
          },
        ],
      },
      {
        path: 'opinion-statement',
        data: { pageTitle: 'Opinion statement', groupKey: 'OPINION_STATEMENT', breadcrumb: true },
        component: ReviewOpinionStatementComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'non-conformities',
        data: { pageTitle: 'Uncorrected non-conformities', groupKey: 'UNCORRECTED_NON_CONFORMITIES', breadcrumb: true },
        component: NonConformitiesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'non-compliances',
        data: { pageTitle: 'Uncorrected non-compliances', groupKey: 'UNCORRECTED_NON_COMPLIANCES', breadcrumb: true },
        component: NonCompliancesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'recommended-improvements',
        data: { pageTitle: 'Recommended improvements', groupKey: 'RECOMMENDED_IMPROVEMENTS', breadcrumb: true },
        component: RecommendedImprovementsComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AerRoutingModule {}
