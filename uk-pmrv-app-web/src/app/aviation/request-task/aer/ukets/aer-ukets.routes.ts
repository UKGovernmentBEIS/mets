import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { aerAdditionalDocumentsFormProvider } from '@aviation/request-task/aer/shared/additional-documents/additional-documents-form.provider';
import { AerReviewDecisionGroupFormProvider } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group-form.provider';
import { AerVerificationReviewDecisionGroupFormProvider } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group-form-provider';
import { AggregatedConsumptionFlightDataFormProvider } from '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data-form.provider';
import { AircraftTypesDataFormProvider } from '@aviation/request-task/aer/shared/aircraft-types-data';
import { MonitoringPlanChangesFormProvider } from '@aviation/request-task/aer/shared/monitoring-plan-changes';
import { ReportingObligationFormProvider } from '@aviation/request-task/aer/shared/reporting-obligation';
import { DataGapsFormProvider } from '@aviation/request-task/aer/ukets/tasks/data-gaps/data-gaps-form.provider';
import { AerEmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { AerMonitoringApproachFormProvider } from '@aviation/request-task/aer/ukets/tasks/monitoring-approach/monitoring-approach-form.provider';
import { OperatorDetailsFormProvider } from '@aviation/request-task/aer/ukets/tasks/operator-details';
import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';
import { canActivateAerReturnForAmends } from '@aviation/request-task/containers/aer-return-for-amends/aer-return-for-amends.guard';
import { ReturnForAmendsPageComponent } from '@aviation/request-task/containers/aer-return-for-amends/return-for-amends-page.component';

import { RequestTaskStore } from '../../store';
import { TASK_FORM_PROVIDER } from '../../task-form.provider';
import { AER_VERIFY_CHILD_ROUTES } from './aer-verify/aer-verify.routes';

const canDeactivateAer: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const AER_CHILD_ROUTES: Routes = [
  {
    path: 'additional-docs',
    providers: [aerAdditionalDocumentsFormProvider],
    loadChildren: () =>
      import('@aviation/request-task/aer/shared/additional-documents/additional-documents.routes').then(
        (r) => r.AER_ADDITIONAL_DOCUMENTS_ROUTES,
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
    path: 'aggregated-consumption-and-flight-data',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AggregatedConsumptionFlightDataFormProvider }],
    loadChildren: () =>
      import(
        '@aviation/request-task/aer/shared/aggregated-consumption-flight-data/aggregated-consumption-flight-data.routes'
      ).then((r) => r.AER_AGGREGATED_CONSUMPTION_FLIGHT_DATA_ROUTES),
  },
  {
    path: 'emissions-reduction-claim',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AerEmissionsReductionClaimFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/emissions-reduction-claim/emissions-reduction-claim.routes').then(
        (r) => r.AER_EMISSIONS_REDUCTION_CLAIM_ROUTES,
      ),
  },
  {
    path: 'total-emissions',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: TotalEmissionsFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions.routes').then(
        (r) => r.AER_TOTAL_EMISSIONS_ROUTES,
      ),
  },
  {
    path: 'data-gaps',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: DataGapsFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/data-gaps/data-gaps.routes').then((r) => r.AER_DATA_GAPS_ROUTES),
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
    path: 'monitoring-approach',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AerMonitoringApproachFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/monitoring-approach/monitoring-approach.routes').then(
        (r) => r.AER_MONITORING_APPROACH_ROUTES,
      ),
  },
  {
    path: 'operator-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider }],
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/operator-details/operator-details.routes').then(
        (r) => r.AER_OPERATOR_DETAILS_ROUTES,
      ),
  },
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
    path: 'send-report',
    loadChildren: () =>
      import('@aviation/request-task/aer/ukets/tasks/send-report/send-report.routes').then(
        (r) => r.AER_SEND_REPORT_ROUTES,
      ),
  },
  {
    path: 'changes-requested',
    data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
    loadComponent: () =>
      import('@aviation/shared/components/aer-changes-requested/aer-changes-requested.component').then(
        (c) => c.AerChangesRequestedComponent,
      ),
  },
];

export const AER_UK_ETS_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateAer],
    children: AER_CHILD_ROUTES,
  },
  {
    path: 'review',
    canDeactivate: [canDeactivateAer],
    providers: [AerReviewDecisionGroupFormProvider],
    children: [
      ...AER_CHILD_ROUTES,
      {
        path: 'aer-verify',
        providers: [AerVerificationReviewDecisionGroupFormProvider],
        children: AER_VERIFY_CHILD_ROUTES,
      },
      {
        path: 'return-for-amends',
        canActivate: [canActivateAerReturnForAmends],
        component: ReturnForAmendsPageComponent,
      },
    ],
  },
];
