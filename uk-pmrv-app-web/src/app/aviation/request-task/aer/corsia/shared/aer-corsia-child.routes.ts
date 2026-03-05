import { Routes } from '@angular/router';

import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';

export const AER_CORSIA_CHILD_ROUTES: Routes = [
  {
    path: 'service-contact-details',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.serviceContactDetails, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/shared/tasks/service-contact-details/service-contact-details.component'
      ).then((c) => c.ServiceContactDetailsComponent),
  },
  {
    path: 'operator-details',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.operatorDetails, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/operator-details/operator-details.component').then(
        (c) => c.OperatorDetailsComponent,
      ),
  },
  {
    path: 'monitoring-plan-changes',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.aerMonitoringPlanChanges, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/shared/tasks/monitoring-plan-changes/monitoring-plan-changes.component'
      ).then((c) => c.MonitoringPlanChangesComponent),
  },
  {
    path: 'monitoring-approach',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.monitoringApproach, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/monitoring-approach/monitoring-approach.component').then(
        (c) => c.MonitoringApproachComponent,
      ),
  },
  {
    path: 'aggregated-consumption-and-flight-data',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.aggregatedEmissionsData, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/shared/tasks/aggregated-consumption-and-flight-data/aggregated-consumption-and-flight-data.component'
      ).then((c) => c.AggregatedConsumptionAndFlightDataComponent),
  },
  {
    path: 'aircraft-types-data',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.aviationAerAircraftData, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/aircraft-types-data/aircraft-types-data.component').then(
        (c) => c.AircraftTypesDataComponent,
      ),
  },
  {
    path: 'emissions-reduction-claim',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.emissionsReductionClaim, breadcrumb: true },
    loadComponent: () =>
      import(
        '@aviation/request-task/aer/corsia/shared/tasks/emissions-reduction-claim/emissions-reduction-claim.component'
      ).then((c) => c.EmissionsReductionClaimComponent),
  },
  {
    path: 'data-gaps',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.dataGaps, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/data-gaps/data-gaps.component').then(
        (c) => c.DataGapsComponent,
      ),
  },
  {
    path: 'additional-docs',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.additionalDocuments, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/additional-docs/additional-docs.component').then(
        (c) => c.AdditionalDocsComponent,
      ),
  },
  {
    path: 'confidentiality',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.confidentiality, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/confidentiality/confidentiality.component').then(
        (c) => c.ConfidentialityComponent,
      ),
  },
  {
    path: 'total-emissions',
    data: { pageTitle: aerReviewCorsiaHeaderTaskMap.totalEmissionsCorsia, breadcrumb: true },
    loadComponent: () =>
      import('@aviation/request-task/aer/corsia/shared/tasks/total-emissions/total-emissions.component').then(
        (c) => c.TotalEmissionsComponent,
      ),
  },
];
