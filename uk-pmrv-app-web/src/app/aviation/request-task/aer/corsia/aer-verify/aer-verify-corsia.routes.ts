import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { GeneralInformationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/general-information-form.provider';
import { IndependentReviewFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review-form.provider';
import { ProcessAnalysisFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/process-analysis-form.provider';
import { canActivateSendReport } from '@aviation/request-task/aer/corsia/aer-verify/tasks/send-report/send-report.guard';
import { TimeAllocationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/time-allocation/time-allocation-form.provider';
import { UncorrectedNonConformitiesFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { VerifiersConclusionsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifiers-conclusions/verifiers-conclusions-form.provider';
import { EmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { OverallDecisionFormProvider } from '@aviation/request-task/aer/shared/overall-decision/overall-decision-form.provider';
import { RecommendedImprovementsFormProvider } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements-form.provider';
import { UncorrectedMisstatementsFormProvider } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements-form.provider';
import { UncorrectedNonCompliancesFormProvider } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

const canDeactivateAerVerifyCorsia: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

const AER_VERIFY_CORSIA_CHILD_ROUTES: Routes = [
  {
    path: 'verifier-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider }],
    loadChildren: () =>
      import('./tasks/verifier-details/verifier-details.routes').then((r) => r.AER_CORSIA_VERIFIER_DETAILS_ROUTES),
  },
  {
    path: 'time-allocation',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: TimeAllocationFormProvider }],
    loadChildren: () =>
      import('./tasks/time-allocation/time-allocation.routes').then((r) => r.AER_CORSIA_TIME_ALLOCATION_ROUTES),
  },
  {
    path: 'general-information',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: GeneralInformationFormProvider }],
    loadChildren: () =>
      import('./tasks/general-information/general-information.routes').then(
        (r) => r.AER_CORSIA_GENERAL_INFORMATION_ROUTES,
      ),
  },
  {
    path: 'process-analysis',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ProcessAnalysisFormProvider }],
    loadChildren: () =>
      import('./tasks/process-analysis/process-analysis.routes').then((r) => r.AER_CORSIA_PROCESS_ANALYSIS_ROUTES),
  },
  {
    path: 'verify-monitoring-approach',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider }],
    loadChildren: () =>
      import('./tasks/verify-monitoring-approach/monitoring-approach.routes').then(
        (r) => r.AER_CORSIA_VERIFY_MONITORING_APPROACH_ROUTES,
      ),
  },
  {
    path: 'verify-emissions-reduction-claim',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
    loadChildren: () =>
      import('./tasks/verify-emissions-reduction-claim/emissions-reduction-claim.routes').then(
        (r) => r.AER_CORSIA_EMISSIONS_REDUCTION_CLAIM_ROUTES,
      ),
  },
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
        (r) => r.AER_CORSIA_UNCORRECTED_NON_COMFORMITIES_ROUTES,
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
    path: 'verifiers-conclusions',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: VerifiersConclusionsFormProvider }],
    loadChildren: () =>
      import('./tasks/verifiers-conclusions/verifiers-conclusions.routes').then(
        (r) => r.AER_CORSIA_VERIFIERS_CONCLUSIONS_ROUTES,
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
  {
    path: 'independent-review',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: IndependentReviewFormProvider }],
    loadChildren: () =>
      import('./tasks/independent-review/independent-review.routes').then(
        (r) => r.AER_CORSIA_INDEPENDENT_REVIEW_ROUTES,
      ),
  },
  {
    path: 'send-report',
    canActivate: [canActivateSendReport],
    loadComponent: () => import('./tasks/send-report/send-report.component').then((c) => c.SendReportComponent),
  },
];

const AER_CORSIA_CHILD_ROUTES: Routes = [
  {
    path: 'service-contact-details',
    loadComponent: () =>
      import('./tasks/service-contact-details/service-contact-details.component').then(
        (c) => c.ServiceContactDetailsComponent,
      ),
  },
  {
    path: 'operator-details',
    loadComponent: () =>
      import('./tasks/operator-details/operator-details.component').then((c) => c.OperatorDetailsComponent),
  },
  {
    path: 'monitoring-plan-changes',
    loadComponent: () =>
      import('./tasks/monitoring-plan-changes/monitoring-plan-changes.component').then(
        (c) => c.MonitoringPlanChangesComponent,
      ),
  },
  {
    path: 'monitoring-approach',
    loadComponent: () =>
      import('./tasks/monitoring-approach/monitoring-approach.component').then((c) => c.MonitoringApproachComponent),
  },
  {
    path: 'aggregated-consumption-and-flight-data',
    loadComponent: () =>
      import('./tasks/aggregated-consumption-and-flight-data/aggregated-consumption-and-flight-data.component').then(
        (c) => c.AggregatedConsumptionAndFlightDataComponent,
      ),
  },
  {
    path: 'aircraft-types-data',
    loadComponent: () =>
      import('./tasks/aircraft-types-data/aircraft-types-data.component').then((c) => c.AircraftTypesDataComponent),
  },
  {
    path: 'emissions-reduction-claim',
    loadComponent: () =>
      import('./tasks/emissions-reduction-claim/emissions-reduction-claim.component').then(
        (c) => c.EmissionsReductionClaimComponent,
      ),
  },
  {
    path: 'data-gaps',
    loadComponent: () => import('./tasks/data-gaps/data-gaps.component').then((c) => c.DataGapsComponent),
  },
  {
    path: 'additional-docs',
    loadComponent: () =>
      import('./tasks/additional-docs/additional-docs.component').then((c) => c.AdditionalDocsComponent),
  },
  {
    path: 'confidentiality',
    loadComponent: () =>
      import('./tasks/confidentiality/confidentiality.component').then((c) => c.ConfidentialityComponent),
  },
  {
    path: 'total-emissions',
    loadComponent: () =>
      import('./tasks/total-emissions/total-emissions.component').then((c) => c.TotalEmissionsComponent),
  },
];

export const AER_VERIFY_CORSIA_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAerVerifyCorsia],
    children: [...AER_VERIFY_CORSIA_CHILD_ROUTES, ...AER_CORSIA_CHILD_ROUTES],
  },
];
