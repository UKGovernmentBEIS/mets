import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { canActivateAdditionalDocuments } from '@aviation/request-task/aer/shared/additional-documents/additional-documents.guards';
import { aerAdditionalDocumentsFormProvider } from '@aviation/request-task/aer/shared/additional-documents/additional-documents-form.provider';
import { canActivateAggregatedConsumptionFlightData } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data.guards';
import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import {
  AircraftTypesDataFormProvider,
  canActivateAircraftTypesData,
} from '@aviation/request-task/aer/shared/aircraft-types-data';
import { canActivateMonitoringPlanChanges } from '@aviation/request-task/aer/shared/monitoring-plan-changes/monitoring-plan-changes.guards';
import { MonitoringPlanChangesFormProvider } from '@aviation/request-task/aer/shared/monitoring-plan-changes/monitoring-plan-changes-form.provider';
import { OverallDecisionFormProvider } from '@aviation/request-task/aer/shared/overall-decision/overall-decision-form.provider';
import { RecommendedImprovementsFormProvider } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements-form.provider';
import { UncorrectedMisstatementsFormProvider } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements-form.provider';
import { UncorrectedNonCompliancesFormProvider } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances-form.provider';
import { MaterialityLevelFormProvider } from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/materiality-level-form.provider';
import { canActivateAviationAerDataGaps } from '@aviation/request-task/aer/ukets/tasks/data-gaps/data-gaps.guard';
import { DataGapsFormProvider } from '@aviation/request-task/aer/ukets/tasks/data-gaps/data-gaps-form.provider';
import { canActivateAerEmissionsReductionClaim } from '@aviation/request-task/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim.guard';
import { AerEmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { AerMonitoringApproachFormProvider } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach';
import { canActivateMonitoringApproach } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach/monitoring-approach.guards';
import { OperatorDetailsFormProvider } from '@aviation/request-task/aer/ukets/tasks/operator-details';
import { canActivateOperatorDetails } from '@aviation/request-task/aer/ukets/tasks/operator-details/operator-details.guard';
import { canActivateTotalEmissions } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions.guard';
import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { ComplianceMonitoringFormProvider } from './tasks/compliance-monitoring/compliance-monitoring-form.provider';
import { DataGapsMethodologiesFormProvider } from './tasks/data-gaps-methodologies/data-gaps-methodologies-form.provider';
import { EtsComplianceRulesFormProvider } from './tasks/ets-compliance-rules/ets-compliance-rules-form.provider';
import { OpinionStatementFormProvider } from './tasks/opinion-statement/opinion-statement-form.provider';
import { UncorrectedNonConformitiesFormProvider } from './tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { VerifierDetailsFormProvider } from './tasks/verifier-details/verifier-details-form.provider';
import { VerifyEmissionsReductionClaimFormProvider } from './tasks/verify-emissions-reduction-claim/verify-emissions-reduction-claim-form.provider';

const canDeactivateAerVerify: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const AER_VERIFY_CHILD_ROUTES: Routes = [
  // AER Verifier's tasks
  {
    path: 'verifier-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider }],
    loadChildren: () =>
      import('./tasks/verifier-details/verifier-details.routes').then((r) => r.AER_VERIFIER_DETAILS_ROUTES),
  },
  {
    path: 'opinion-statement',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OpinionStatementFormProvider }],
    loadChildren: () =>
      import('./tasks/opinion-statement/opinion-statement.routes').then((r) => r.AER_OPINIOON_STATEMENT_ROUTES),
  },
  {
    path: 'ets-compliance-rules',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: EtsComplianceRulesFormProvider }],
    loadChildren: () =>
      import('./tasks/ets-compliance-rules/ets-compliance-rules.routes').then((r) => r.AER_ETS_COMPLIANCE_RULES_ROUTES),
  },
  {
    path: 'compliance-monitoring',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ComplianceMonitoringFormProvider }],
    loadChildren: () =>
      import('./tasks/compliance-monitoring/compliance-monitoring.routes').then(
        (r) => r.AER_COMPLIANCE_MONITORING_ROUTES,
      ),
  },
  {
    path: 'verify-emissions-reduction-claim',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: VerifyEmissionsReductionClaimFormProvider }],
    loadChildren: () =>
      import('./tasks/verify-emissions-reduction-claim/verify-emissions-reduction-claim.routes').then(
        (r) => r.AER_VERIFY_EMISSIONS_REDUCTION_CLAIM_ROUTES,
      ),
  },
  {
    path: 'overall-decision',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/shared/overall-decision/overall-decision.routes').then(
        (r) => r.AER_OVERALL_DECISION_ROUTES,
      ),
  },
  //AER VERIFIER FINDINGS
  {
    path: 'uncorrected-misstatements',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: UncorrectedMisstatementsFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.routes').then(
        (r) => r.AER_UNCORRECTED_MISSTATEMENTS_ROUTES,
      ),
  },

  {
    path: 'uncorrected-non-conformities',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonConformitiesFormProvider }],
    loadChildren: () =>
      import('./tasks/uncorrected-non-conformities/uncorrected-non-conformities.routes').then(
        (r) => r.AER_UNCORRECTED_NON_CONFORMITIES_ROUTES,
      ),
  },
  {
    path: 'uncorrected-non-compliances',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: UncorrectedNonCompliancesFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.routes').then(
        (r) => r.AER_UNCORRECTED_NON_COMPLIANCES_ROUTES,
      ),
  },
  {
    path: 'recommended-improvements',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: RecommendedImprovementsFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.routes').then(
        (r) => r.AER_RECOMMENDED_IMPROVEMENTS_ROUTES,
      ),
  },
  {
    path: 'data-gaps-methodologies',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: DataGapsMethodologiesFormProvider }],
    loadChildren: () =>
      import('./tasks/data-gaps-methodologies/data-gaps-methodologies.routes').then(
        (r) => r.AER_DATA_GAPS_METHODOLOGIES_ROUTES,
      ),
  },
  {
    path: 'materiality-level',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MaterialityLevelFormProvider }],
    loadChildren: () =>
      import('./tasks/materiality-level/materiality-level.routes').then((r) => r.AER_MATERIALITY_LEVEL_ROUTES),
  },
  // AER operator's completed tasks
  {
    path: 'service-contact-details-summary',
    data: { pageTitle: 'Service contact details', breadcrumb: true },
    loadComponent: () => import('./tasks/service-contact-details/service-contact-details-summary.component'),
  },
  {
    path: 'operator-details-summary',
    canActivate: [canActivateOperatorDetails],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Operator details' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider }],
    loadComponent: () => import('./tasks/operator-details/operator-details-summary.component'),
  },
  {
    path: 'monitoring-plan-changes-summary',
    canActivate: [canActivateMonitoringPlanChanges],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring plan changes' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringPlanChangesFormProvider }],
    loadComponent: () => import('./tasks/monitoring-plan-changes/monitoring-plan-changes-summary.component'),
  },
  {
    path: 'monitoring-approach-summary',
    canActivate: [canActivateMonitoringApproach],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Monitoring approach' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AerMonitoringApproachFormProvider }],
    loadComponent: () => import('./tasks/monitoring-approach/monitoring-approach-summary.component'),
  },
  {
    path: 'aggregated-consumption-flight-data-summary',
    canActivate: [canActivateAggregatedConsumptionFlightData],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Aggregated consumption and flight data' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AggregatedConsumptionFlightDataFormProvider }],
    loadComponent: () =>
      import('./tasks/aggregated-consumption-flight-data/aggregated-consumption-flight-data-summary.component'),
  },
  {
    path: 'aircraft-types-data-summary',
    canActivate: [canActivateAircraftTypesData],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Aircraft types data' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AircraftTypesDataFormProvider }],
    loadComponent: () => import('./tasks/aircraft-types-data/aircraft-types-data-summary.component'),
  },
  {
    path: 'saf-summary',
    canActivate: [canActivateAerEmissionsReductionClaim],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Emissions reduction claim' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AerEmissionsReductionClaimFormProvider }],
    loadComponent: () => import('./tasks/emissions-reduction-claim/emissions-reduction-claim-summary.component'),
  },
  {
    path: 'data-gaps-summary',
    canActivate: [canActivateAviationAerDataGaps],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Data gaps' },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider }],
    loadComponent: () => import('./tasks/data-gaps/data-gaps-summary.component'),
  },
  {
    path: 'additional-documents-summary',
    canActivate: [canActivateAdditionalDocuments],
    data: { pageTitle: 'Check your answers', breadcrumb: 'Additional documents and information' },
    providers: [aerAdditionalDocumentsFormProvider],
    loadComponent: () => import('./tasks/additional-documents/additional-documents-summary.component'),
  },
  {
    path: 'total-emissions-summary',
    canActivate: [canActivateTotalEmissions],
    data: { pageTitle: 'Total emissions', breadcrumb: true },
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: TotalEmissionsFormProvider }],
    loadComponent: () => import('./tasks/total-emissions/total-emissions-summary.component'),
  },
];

export const AER_VERIFY_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAerVerify],
    children: AER_VERIFY_CHILD_ROUTES,
  },
  // Send AER Verifier's report to operator task
  {
    path: 'send-report',
    loadChildren: () =>
      import('./tasks/send-report-to-operator/send-report-to-operator.routes').then(
        (r) => r.AER_VERIFY_SEND_REPORT_ROUTES,
      ),
  },
];
