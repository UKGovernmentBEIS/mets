import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { AerReturnForAmendsComponent } from '@aviation/request-action/aer/ukets/return-for-amends/aer-return-for-amends.component';
import { RequestActionStore } from '@aviation/request-action/store';

const canDeactivateAer: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

export const AER_UKETS_ROUTES: Routes = [
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
        children: [
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
              import('@aviation/request-action/aer/ukets/tasks/operator-details/operator-details.component'),
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
              import('@aviation/request-action/aer/ukets/tasks/monitoring-approach/monitoring-approach.component'),
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
            data: { pageTitle: 'Emissions reduction claim', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim.component'
              ),
          },
          {
            path: 'data-gaps',
            data: { pageTitle: 'Data gaps', breadcrumb: true },
            loadComponent: () => import('@aviation/request-action/aer/ukets/tasks/data-gaps/data-gaps.component'),
          },
          {
            path: 'additional-docs',
            data: { pageTitle: 'Additional documents and information', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/shared/additional-docs/additional-docs.component'),
          },
          {
            path: 'total-emissions',
            data: { pageTitle: 'Total emissions', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/total-emissions/total-emissions.component'),
          },
        ],
      },
      {
        path: 'verify-submitted',
        children: [
          // Verifier assessment routes
          {
            path: 'verifier-details',
            data: { pageTitle: 'Verifier details', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/verifier-details/verifier-details.component'),
          },
          {
            path: 'opinion-statement',
            data: { pageTitle: 'Opinion statement', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/opinion-statement/opinion-statement.component'),
          },
          {
            path: 'ets-compliance-rules',
            data: { pageTitle: 'Compliance with ETS rules', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/ets-compliance-rules/ets-compliance-rules.component'),
          },
          {
            path: 'compliance-monitoring',
            data: { pageTitle: 'Compliance with monitoring and reporting principles', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/compliance-monitoring/compliance-monitoring.component'),
          },
          {
            path: 'verify-emissions-reduction-claim',
            data: { pageTitle: 'Verify the emissions reduction claim', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/ukets/tasks/verify-emissions-reduction-claim/verify-emissions-reduction-claim.component'
              ),
          },
          {
            path: 'overall-decision',
            data: { pageTitle: 'Overall decision', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/shared/overall-decision/overall-decision.component'),
          },
          // Verifier findings routes
          {
            path: 'uncorrected-misstatements',
            data: { pageTitle: 'Uncorrected misstatements', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/shared/uncorrected-misstatements/uncorrected-misstatements.component'
              ),
          },
          {
            path: 'uncorrected-non-conformities',
            data: { pageTitle: 'Uncorrected non-conformities', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/ukets/tasks/uncorrected-non-conformities/uncorrected-non-conformities.component'
              ),
          },
          {
            path: 'uncorrected-non-compliances',
            data: { pageTitle: 'Uncorrected non-compliances', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/ukets/tasks/uncorrected-non-compliances/uncorrected-non-compliances.component'
              ),
          },
          {
            path: 'recommended-improvements',
            data: { pageTitle: 'Recommended improvements', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/shared/recommended-improvements/recommended-improvements.component'),
          },
          {
            path: 'data-gaps-methodologies',
            data: { pageTitle: 'Methodologies to close data gaps', breadcrumb: true },
            loadComponent: () =>
              import(
                '@aviation/request-action/aer/ukets/tasks/data-gaps-methodologies/data-gaps-methodologies.component'
              ),
          },
          {
            path: 'materiality-level',
            data: { pageTitle: 'Materiality level and reference documents held', breadcrumb: true },
            loadComponent: () =>
              import('@aviation/request-action/aer/ukets/tasks/materiality-level/materiality-level.component'),
          },
        ],
      },
      {
        path: 'return-for-amends',
        component: AerReturnForAmendsComponent,
      },
    ],
  },
];
