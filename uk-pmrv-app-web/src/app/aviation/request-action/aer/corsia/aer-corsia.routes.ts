import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestActionStore } from '@aviation/request-action/store';

const canDeactivateAer: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

const AER_VERIFY_RELATED_CORSIA_ROUTES: Routes = [
  {
    path: 'verifier-details',
    data: { pageTitle: 'Verifier details and impartiality', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/verifier-details/verifier-details.component'),
  },
  {
    path: 'time-allocation',
    data: { pageTitle: 'Time allocation and scope', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/corsia/tasks/time-allocation/time-allocation.component'),
  },
  {
    path: 'general-information',
    data: { pageTitle: 'Verification criteria and operator data', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/general-information/general-information.component'),
  },
  {
    path: 'process-analysis',
    data: { pageTitle: 'Process and analysis details', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/process-analysis/process-analysis.component'),
  },
  {
    path: 'verify-monitoring-approach',
    data: { pageTitle: 'Monitoring approach and emissions', breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-action/aer/corsia/tasks/verify-monitoring-approach/verify-monitoring-approach.component'
      ),
  },
  {
    path: 'verify-emissions-reduction-claim',
    data: { pageTitle: 'Verify the emissions reduction claim', breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-action/aer/corsia/tasks/verify-emissions-reduction-claim/verify-emissions-reduction-claim.component'
      ),
  },
  {
    path: 'uncorrected-misstatements',
    data: { pageTitle: 'Uncorrected misstatements', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/uncorrected-misstatements/uncorrected-misstatements.component'),
  },
  {
    path: 'uncorrected-non-conformities',
    data: { pageTitle: 'Uncorrected non-conformities', breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-action/aer/corsia/tasks/uncorrected-non-conformities/uncorrected-non-conformities.component'
      ),
  },
  {
    path: 'uncorrected-non-compliances',
    data: { pageTitle: 'Uncorrected non-compliances', breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-action/aer/corsia/tasks/uncorrected-non-compliances/uncorrected-non-compliances.component'
      ),
  },
  {
    path: 'recommended-improvements',
    data: { pageTitle: 'Recommended improvements', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/recommended-improvements/recommended-improvements.component'),
  },
  {
    path: 'verifiers-conclusions',
    data: { pageTitle: 'Conclusions on data quality and materiality', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/verifiers-conclusions/verifiers-conclusions.component'),
  },
  {
    path: 'overall-decision',
    data: { pageTitle: 'Overall decision', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/shared/overall-decision/overall-decision.component'),
  },
  {
    path: 'independent-review',
    data: { pageTitle: 'Independent review', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/independent-review/independent-review.component'),
  },
];

const AER_RELATED_CORSIA_ROUTES: Routes = [
  {
    path: 'reporting-obligation',
    data: { pageTitle: 'Reporting obligation', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/reporting-obligation/reporting-obligation.component'),
  },
  {
    path: 'service-contact-details',
    data: { pageTitle: 'Service contact details', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/service-contact-details/service-contact-details.component'),
  },
  {
    path: 'operator-details',
    data: { pageTitle: 'Operator details', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/operator-details/operator-details.component'),
  },
  {
    path: 'monitoring-plan-changes',
    data: { pageTitle: 'Monitoring plan changes', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/monitoring-plan-changes/monitoring-plan-changes.component'),
  },
  {
    path: 'monitoring-approach',
    data: { pageTitle: 'Monitoring approach', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/monitoring-approach/monitoring-approach.component'),
  },
  {
    path: 'aggregated-consumption-and-flight-data',
    data: { pageTitle: 'Aggregated consumption and flight data', breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-action/aer/shared/aggregated-consumption-and-flight-data/aggregated-consumption-and-flight-data.component'
      ),
  },
  {
    path: 'aircraft-types-data',
    data: { pageTitle: 'Aircraft types data', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/shared/aircraft-types-data/aircraft-types-data.component'),
  },
  {
    path: 'emissions-reduction-claim',
    data: { pageTitle: 'CORSIA eligible fuels reduction claim', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-action/aer/corsia/tasks/emissions-reduction-claim/emissions-reduction-claim.component'),
  },
  {
    path: 'data-gaps',
    data: { pageTitle: 'Data gaps', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/corsia/tasks/data-gaps/data-gaps.component'),
  },
  {
    path: 'additional-docs',
    data: { pageTitle: 'Additional documents and information', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/shared/additional-docs/additional-docs.component'),
  },
  {
    path: 'confidentiality',
    data: { pageTitle: 'Confidentiality statement', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/corsia/tasks/confidentiality/confidentiality.component'),
  },
  {
    path: 'total-emissions',
    data: { pageTitle: 'Total emissions', breadcrumb: true },
    loadComponent: () => import('@aviation/request-action/aer/corsia/tasks/total-emissions/total-emissions.component'),
  },
];

export const AER_CORSIA_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAer],
    children: [
      {
        path: 'aer-completed',
        data: { pageTitle: 'Emissions report reviewed', breadcrumb: true },
        loadComponent: () =>
          import('@aviation/request-action/aer/shared/aer-completed/aer-completed.component').then(
            (c) => c.AerCompletedComponent,
          ),
      },
      {
        path: 'submitted',
        children: [...AER_RELATED_CORSIA_ROUTES, ...AER_VERIFY_RELATED_CORSIA_ROUTES],
      },
    ],
  },
];
