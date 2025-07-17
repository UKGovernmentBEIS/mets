import { Routes } from '@angular/router';

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
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const AER_VERIFY_CORSIA_CHILD_ROUTES: Routes = [
  {
    path: 'verifier-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: VerifierDetailsFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/verifier-details/verifier-details.routes').then(
        (r) => r.AER_CORSIA_VERIFIER_DETAILS_ROUTES,
      ),
  },
  {
    path: 'time-allocation',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: TimeAllocationFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/time-allocation/time-allocation.routes').then(
        (r) => r.AER_CORSIA_TIME_ALLOCATION_ROUTES,
      ),
  },
  {
    path: 'general-information',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: GeneralInformationFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/general-information/general-information.routes').then(
        (r) => r.AER_CORSIA_GENERAL_INFORMATION_ROUTES,
      ),
  },
  {
    path: 'process-analysis',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ProcessAnalysisFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/process-analysis/process-analysis.routes').then(
        (r) => r.AER_CORSIA_PROCESS_ANALYSIS_ROUTES,
      ),
  },
  {
    path: 'verify-monitoring-approach',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/verify-monitoring-approach/monitoring-approach.routes').then(
        (r) => r.AER_CORSIA_VERIFY_MONITORING_APPROACH_ROUTES,
      ),
  },
  {
    path: 'verify-emissions-reduction-claim',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
    loadChildren: () =>
      import('../aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim.routes').then(
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
      import('../aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities.routes').then(
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
      import('../aer-verify/tasks/verifiers-conclusions/verifiers-conclusions.routes').then(
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
      import('../aer-verify/tasks/independent-review/independent-review.routes').then(
        (r) => r.AER_CORSIA_INDEPENDENT_REVIEW_ROUTES,
      ),
  },
  {
    path: 'send-report',
    canActivate: [canActivateSendReport],
    loadComponent: () =>
      import('../aer-verify/tasks/send-report/send-report.component').then((c) => c.SendReportComponent),
  },
];
