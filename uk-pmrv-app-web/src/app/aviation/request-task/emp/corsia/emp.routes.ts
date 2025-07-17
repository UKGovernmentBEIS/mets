import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { AbbreviationsFormProvider } from '../shared/abbreviations';
import { AdditionalDocumentsFormProvider } from '../shared/additional-documents/additional-documents-form.provider';
import { BlockHourProceduresFormProvider } from '../shared/block-hour';
import { BlockProceduresFormProvider } from '../shared/block-procedures';
import { EmpReviewDecisionGroupFormProvider } from '../shared/emp-review-decision-group/emp-review-decision-group-form.provider';
import { EmpVariationReviewDecisionGroupFormProvider } from '../shared/emp-variation-review-decision-group/emp-variation-review-decision-group.provider';
import { FuelUpliftProceduresFormProvider } from '../shared/fuel-uplift-procedures';
import { MethodAProceduresFormProvider } from '../shared/method-a-procedures/method-a-procedures-form.provider';
import { MethodBProceduresFormProvider } from '../shared/method-b-procedures/method-b-procedures-form.provider';
import {
  canActivateEmpNotifyOperator,
  canActivateEmpVariationRegLedNotifyOperator,
} from '../shared/notify-operator/notify-operator.guard';
import { OverallDecisionFormProvider } from '../shared/overall-decision/overall-decision-form.provider';
import {
  canActivateEmpPeerReview,
  canActivateEmpVariationRegLedPeerReview,
} from '../shared/peer-review/peer-review.guard';
import { canActivateEmpReturnForAmends } from '../shared/return-for-amends/return-for-amends.guard';
import { dataGapsFormProvider } from './tasks/data-gaps/data-gaps-form.provider';
import { EmissionSourcesCorsiaFormProvider } from './tasks/emission-sources/emission-sources-form.provider';
import { FlightProceduresFormProvider } from './tasks/flight-procedures/flight-procedures-form.provider';
import { ManagementProceduresCorsiaFormProvider } from './tasks/management-procedures/management-procedures-form.provider';
import { MonitoringApproachCorsiaFormProvider } from './tasks/monitoring-approach/monitoring-approach-form.provider';
import { OperatorDetailsCorsiaFormProvider } from './tasks/operator-details/operator-details-form.provider';
import { VariationDetailsFormProvider } from './tasks/variation-details/variation-details-form.provider';

const canDeactivateEmp: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const EMP_CHILD_ROUTES: Routes = [
  {
    path: 'additional-docs',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AdditionalDocumentsFormProvider }],
    loadChildren: () =>
      import('../shared/additional-documents/additional-documents.routes').then(
        (r) => r.EMP_ADDITIONAL_DOCUMENTS_ROUTES,
      ),
  },
  {
    path: 'abbreviations',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AbbreviationsFormProvider }],
    loadChildren: () => import('../shared/abbreviations/abbreviations.routes').then((r) => r.EMP_ABBREVIATION_ROUTES),
  },

  {
    path: 'submission',
    loadChildren: () => import('../shared/submission/submission.routes').then((r) => r.EMP_SUBMISSION_ROUTES),
  },
  {
    path: 'operator-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsCorsiaFormProvider }],
    loadChildren: () =>
      import('./tasks/operator-details/operator-details.routes').then((r) => r.EMP_OPERATOR_DETAILS_ROUTES),
  },
  {
    path: 'emission-sources',
    providers: [EmissionSourcesCorsiaFormProvider],
    loadChildren: () =>
      import('./tasks/emission-sources/emission-sources.routes').then((r) => r.EMP_EMISSION_SOURCES_ROUTES),
  },
  {
    path: 'data-gaps',
    providers: [dataGapsFormProvider],
    loadChildren: () => import('./tasks/data-gaps/data-gaps.routes').then((r) => r.EMP_DATA_GAPS_ROUTES),
  },
  {
    path: 'method-a-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MethodAProceduresFormProvider }],
    loadChildren: () =>
      import('../shared/method-a-procedures/method-a-procedures.routes').then((r) => r.EMP_METHOD_A_PROCEDURES_ROUTES),
  },
  {
    path: 'method-b-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MethodBProceduresFormProvider }],
    loadChildren: () =>
      import('../shared/method-b-procedures/method-b-procedures.routes').then((r) => r.EMP_METHOD_B_PROCEDURES_ROUTES),
  },
  {
    path: 'block-on-off-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: BlockProceduresFormProvider }],
    loadChildren: () =>
      import('../shared/block-procedures/block-procedures.routes').then((r) => r.EMP_BLOCK_PROCEDURES_ROUTES),
  },
  {
    path: 'fuel-uplift-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: FuelUpliftProceduresFormProvider }],
    loadChildren: () =>
      import('../shared/fuel-uplift-procedures/fuel-uplift-procedures.routes').then(
        (r) => r.EMP_FUEL_UPLIFT_PROCEDURES_ROUTES,
      ),
  },
  {
    path: 'block-hour-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: BlockHourProceduresFormProvider }],
    loadChildren: () =>
      import('../shared/block-hour/block-hour-procedures.routes').then((r) => r.EMP_BLOCK_HOUR_PROCEDURES_ROUTES),
  },
  {
    path: 'service-contact-details',
    loadChildren: () =>
      import('../shared/service-contact-details/service-contact-details.routes').then(
        (r) => r.EMP_SERVICE_CONTACT_DETAILS_ROUTES,
      ),
  },
  {
    path: 'flight-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider }],
    loadChildren: () =>
      import('./tasks/flight-procedures/flight-procedures.routes').then((r) => r.EMP_CORSIA_FLIGHT_PROCEDURES_ROUTES),
  },
  {
    path: 'changes-requested',
    data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
    loadComponent: () =>
      import('../../../shared/components/changes-requested/changes-requested.component').then(
        (c) => c.ChangesRequestedComponent,
      ),
  },
  {
    path: 'management-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresCorsiaFormProvider }],
    loadChildren: () =>
      import('./tasks/management-procedures/management-procedures.routes').then(
        (r) => r.EMP_MANAGEMENT_PROCEDURES_CORSIA_ROUTES,
      ),
  },
  {
    path: 'monitoring-approach',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachCorsiaFormProvider }],
    loadChildren: () =>
      import('./tasks/monitoring-approach/monitoring-approach.routes').then(
        (r) => r.EMP_MONITORING_APPROACH_CORSIA_ROUTES,
      ),
  },
];

const EMP_REVIEW_CHILD_ROUTES: Routes = [
  ...EMP_CHILD_ROUTES,
  {
    path: 'decision',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider }],
    loadChildren: () =>
      import('../shared/overall-decision/overall-decision.routes').then((r) => r.EMP_OVERALL_DECISION_ROUTES),
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Notify Operator of Decision' },
    providers: [PaymentCompletedGuard],
    canActivate: [canActivateEmpNotifyOperator, PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
    loadComponent: () =>
      import('../shared/notify-operator/notify-operator.component').then((c) => c.EmpNotifyOperatorComponent),
  },
  {
    path: 'return-for-amends',
    canActivate: [canActivateEmpReturnForAmends],
    canDeactivate: [PendingRequestGuard],
    loadComponent: () =>
      import('../shared/return-for-amends/return-for-amends.component').then((c) => c.EmpReturnForAmendsComponent),
  },
  {
    path: 'peer-review',
    providers: [PaymentCompletedGuard],
    canActivate: [PaymentCompletedGuard, canActivateEmpPeerReview],
    canDeactivate: [PendingRequestGuard],
    loadComponent: () => import('../shared/peer-review/peer-review.component').then((c) => c.EmpPeerReviewComponent),
  },
];

export const EMP_CORSIA_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateEmp],
    children: [
      {
        path: 'submit',
        children: EMP_CHILD_ROUTES,
      },
      {
        path: 'variation-regulator-led',
        providers: [EmpVariationRegulatorLedDecisionGroupFormProvider],
        children: [
          ...EMP_CHILD_ROUTES,
          {
            path: 'variation-details',
            providers: [{ provide: TASK_FORM_PROVIDER, useClass: VariationDetailsFormProvider }],
            loadChildren: () =>
              import('./tasks/variation-details/variation-details.routes').then((r) => r.EMP_VARIATION_DETAILS_ROUTES),
          },
          {
            path: 'notify-operator',
            data: { pageTitle: 'Notify Operator of Decision' },
            canActivate: [canActivateEmpVariationRegLedNotifyOperator],
            canDeactivate: [PendingRequestGuard],
            loadComponent: () =>
              import('../shared/notify-operator/notify-operator.component').then((c) => c.EmpNotifyOperatorComponent),
          },
          {
            path: 'peer-review',
            canActivate: [canActivateEmpVariationRegLedPeerReview],
            canDeactivate: [PendingRequestGuard],
            loadComponent: () =>
              import('../shared/peer-review/peer-review.component').then((c) => c.EmpPeerReviewComponent),
          },
        ],
      },
      {
        path: 'variation',
        children: [
          ...EMP_CHILD_ROUTES,
          {
            path: 'variation-details',
            providers: [{ provide: TASK_FORM_PROVIDER, useClass: VariationDetailsFormProvider }],
            loadChildren: () =>
              import('./tasks/variation-details/variation-details.routes').then((r) => r.EMP_VARIATION_DETAILS_ROUTES),
          },
          {
            path: 'notify-operator',
            data: { pageTitle: 'Notify Operator of Decision' },
            providers: [PaymentCompletedGuard],
            canActivate: [canActivateEmpNotifyOperator, PaymentCompletedGuard],
            canDeactivate: [PendingRequestGuard],
            loadComponent: () =>
              import('../shared/notify-operator/notify-operator.component').then((c) => c.EmpNotifyOperatorComponent),
          },
          {
            path: 'review',
            providers: [EmpVariationReviewDecisionGroupFormProvider],
            children: [
              ...EMP_REVIEW_CHILD_ROUTES,
              {
                path: 'variation-details',
                providers: [{ provide: TASK_FORM_PROVIDER, useClass: VariationDetailsFormProvider }],
                loadChildren: () =>
                  import('./tasks/variation-details/variation-details.routes').then(
                    (r) => r.EMP_VARIATION_DETAILS_ROUTES,
                  ),
              },
            ],
          },
        ],
      },
      {
        path: 'review',
        providers: [EmpReviewDecisionGroupFormProvider],
        children: EMP_REVIEW_CHILD_ROUTES,
      },
    ],
  },
];
