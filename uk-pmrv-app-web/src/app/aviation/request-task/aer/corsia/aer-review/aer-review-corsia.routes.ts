import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { AER_CORSIA_CHILD_ROUTES } from '@aviation/request-task/aer/corsia/shared/aer-corsia-child.routes';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupFormProvider } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group-form.provider';
import { AerVerificationReviewDecisionGroupFormProvider } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group-form-provider';
import { canActivateAerReturnForAmends } from '@aviation/request-task/containers/aer-return-for-amends/aer-return-for-amends.guard';
import { RequestTaskStore } from '@aviation/request-task/store';

const canDeactivateAerReviewCorsia: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

const AER_REVIEW_VERIFICATION_ROUTES: Routes = [
  {
    path: 'verifier-details',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.verifierDetails, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/aer-review/tasks/verifier-details/verifier-details.component').then(
        (c) => c.VerifierDetailsComponent,
      ),
  },
  {
    path: 'time-allocation',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.timeAllocationScope, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/aer-review/tasks/time-allocation/time-allocation.component').then(
        (c) => c.TimeAllocationComponent,
      ),
  },
  {
    path: 'general-information',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.generalInformation, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/general-information/general-information.component'
      ).then((c) => c.GeneralInformationComponent),
  },
  {
    path: 'process-analysis',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.processAnalysis, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/aer-review/tasks/process-analysis/process-analysis.component').then(
        (c) => c.ProcessAnalysisComponent,
      ),
  },
  {
    path: 'verify-monitoring-approach',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.opinionStatement, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/verify-monitoring-approach/verify-monitoring-approach.component'
      ).then((c) => c.VerifyMonitoringApproachComponent),
  },
  {
    path: 'verify-emissions-reduction-claim',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.emissionsReductionClaimVerification, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/verify-emissions-reduction-claim/verify-emissions-reduction-claim.component'
      ).then((c) => c.VerifyEmissionsReductionClaimComponent),
  },
  {
    path: 'uncorrected-misstatements',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.uncorrectedMisstatements, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/uncorrected-misstatements/uncorrected-misstatements.component'
      ).then((c) => c.UncorrectedMisstatementsComponent),
  },
  {
    path: 'uncorrected-non-conformities',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.uncorrectedNonConformities, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/uncorrected-non-conformities/uncorrected-non-conformities.component'
      ).then((c) => c.UncorrectedNonConformitiesComponent),
  },
  {
    path: 'uncorrected-non-compliances',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.uncorrectedNonCompliances, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/uncorrected-non-compliances/uncorrected-non-compliances.component'
      ).then((c) => c.UncorrectedNonCompliancesComponent),
  },
  {
    path: 'recommended-improvements',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.recommendedImprovements, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/recommended-improvements/recommended-improvements.component'
      ).then((c) => c.RecommendedImprovementsComponent),
  },
  {
    path: 'verifiers-conclusions',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.verifiersConclusions, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/aer-review/tasks/verifiers-conclusions/verifiers-conclusions.component'
      ).then((c) => c.VerifiersConclusionsComponent),
  },
  {
    path: 'overall-decision',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.overallDecision, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/aer-review/tasks/overall-decision/overall-decision.component').then(
        (c) => c.OverallDecisionComponent,
      ),
  },
  {
    path: 'independent-review',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.independentReview, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/aer-review/tasks/independent-review/independent-review.component').then(
        (c) => c.IndependentReviewComponent,
      ),
  },
];

export const AER_REVIEW_CORSIA_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAerReviewCorsia],
    providers: [AerReviewDecisionGroupFormProvider, AerVerificationReviewDecisionGroupFormProvider],
    children: [
      ...AER_REVIEW_VERIFICATION_ROUTES,
      ...AER_CORSIA_CHILD_ROUTES,
      {
        path: 'reporting-obligation',
        data: { pageTitle: aerReviewCorsiaHeaderTaskMap.reportingObligation, breadcrumb: true },
        loadComponent: () =>
          import(
            '@aviation/request-task/aer/corsia/aer-review/tasks/reporting-obligation-details/reporting-obligation-details.component'
          ).then((c) => c.ReportingObligationDetailsComponent),
      },
      {
        path: 'return-for-amends',
        canActivate: [canActivateAerReturnForAmends],
        loadComponent: () =>
          import('@aviation/request-task/containers/aer-return-for-amends/return-for-amends-page.component').then(
            (c) => c.ReturnForAmendsPageComponent,
          ),
      },
    ],
  },
];
