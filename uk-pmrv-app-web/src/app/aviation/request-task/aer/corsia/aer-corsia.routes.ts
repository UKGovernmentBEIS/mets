import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { OperatorDetailsFormProvider } from '@aviation/request-task/aer/corsia/tasks/operator-details';
import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import { AircraftTypesDataFormProvider } from '@aviation/request-task/aer/shared/aircraft-types-data';
import { MonitoringPlanChangesFormProvider } from '@aviation/request-task/aer/shared/monitoring-plan-changes';
import { ReportingObligationFormProvider } from '@aviation/request-task/aer/shared/reporting-obligation';

import { RequestTaskStore } from '../../store';
import { TASK_FORM_PROVIDER } from '../../task-form.provider';
import { aerAdditionalDocumentsFormProvider } from '../shared/additional-documents/additional-documents-form.provider';
import { ConfidentialityFormProvider } from './tasks/confidentiality/confidentiality-form.provider';
import { DataGapsFormProvider } from './tasks/data-gaps/data-gaps-form.provider';
import { EmissionsReductionClaimFormProvider } from './tasks/emissions-reduction-claim/emissions-reduction-claim-form.provider';

const canDeactivateAer: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

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
        path: 'service-contact-details',
        loadChildren: () =>
          import('@aviation/request-task/aer/shared/service-contact-details/service-contact-details.routes').then(
            (r) => r.AER_SERVICE_CONTACT_DETAILS_ROUTES,
          ),
      },
      {
        path: 'reporting-obligation',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: ReportingObligationFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.routes').then(
            (r) => r.AER_REPORTING_OBLIGATION_ROUTES,
          ),
      },
      {
        path: 'monitoring-plan-changes',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringPlanChangesFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/shared/monitoring-plan-changes/monitoring-plan-changes.routes').then(
            (r) => r.AER_MONITORING_PLAN_CHANGES_ROUTES,
          ),
      },
      {
        path: 'operator-details',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/corsia/tasks/operator-details/operator-details.routes').then(
            (r) => r.AER_CORSIA_OPERATOR_DETAILS_ROUTES,
          ),
      },
      {
        path: 'aircraft-types-data',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: AircraftTypesDataFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/shared/aircraft-types-data/aircraft-types-data.routes').then(
            (r) => r.AER_AIRCRAFT_TYPES_DATA_ROUTES,
          ),
      },
      {
        path: 'additional-docs',
        providers: [aerAdditionalDocumentsFormProvider],
        loadChildren: () =>
          import('@aviation/request-task/aer/shared/additional-documents/additional-documents.routes').then(
            (r) => r.AER_ADDITIONAL_DOCUMENTS_ROUTES,
          ),
      },
      {
        path: 'aggregated-consumption-and-flight-data',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: AggregatedConsumptionFlightDataFormProvider }],
        loadChildren: () =>
          import(
            '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data.routes'
          ).then((r) => r.AER_AGGREGATED_CONSUMPTION_FLIGHT_DATA_ROUTES),
      },
      {
        path: 'monitoring-approach',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: AviationAerCorsiaMonitoringApproachFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach.routes').then(
            (r) => r.AER_CORSIA_MONITORING_APPROACH_ROUTES,
          ),
      },
      {
        path: 'emissions-reduction-claim',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
        loadChildren: () =>
          import(
            '@aviation/request-task/aer/corsia/tasks/emissions-reduction-claim/emissions-reduction-claim.routes'
          ).then((r) => r.AER_CORSIA_EMISSIONS_REDUCTION_CLAIM_ROUTES),
      },
      {
        path: 'total-emissions',
        loadChildren: () =>
          import('@aviation/request-task/aer/corsia/tasks/total-emissions/total-emissions.routes').then(
            (r) => r.AER_CORSIA_TOTAL_EMISSIONS_ROUTES,
          ),
      },
      {
        path: 'send-report',
        loadChildren: () =>
          import('@aviation/request-task/aer/corsia/tasks/send-report/send-report.routes').then(
            (r) => r.AER_CORSIA_SEND_REPORT_ROUTES,
          ),
      },
      {
        path: 'data-gaps',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider }],
        loadChildren: () =>
          import('@aviation/request-task/aer/corsia/tasks/data-gaps/data-gaps.routes').then(
            (r) => r.AER_CORSIA_DATA_GAPS_ROUTES,
          ),
      },
      {
        path: 'confidentiality',
        providers: [{ provide: TASK_FORM_PROVIDER, useClass: ConfidentialityFormProvider }],
        loadChildren: () =>
          import('./tasks/confidentiality/confidentiality.routes').then((r) => r.AER_CORSIA_CONFIDENTIALITY_ROUTES),
      },
      {
        path: 'changes-requested',
        data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
        loadComponent: () =>
          import('@aviation/shared/components/aer-changes-requested/aer-changes-requested.component').then(
            (c) => c.AerChangesRequestedComponent,
          ),
      },
    ],
  },
];
