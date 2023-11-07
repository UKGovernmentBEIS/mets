import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import {
  canActivateEmpPeerReview,
  canActivateEmpVariationRegLedPeerReview,
} from '@aviation/request-task/emp/shared/peer-review/peer-review.guard';
import { FlightProceduresFormProvider } from '@aviation/request-task/emp/ukets/tasks/flight-procedures';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { RequestTaskStore } from '../../store';
import { TASK_FORM_PROVIDER } from '../../task-form.provider';
import { AbbreviationsFormProvider } from '../shared/abbreviations';
import { additionalDocumentsFormProvider } from '../shared/additional-documents/additional-documents-form.provider';
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
import { canActivateEmpReturnForAmends } from '../shared/return-for-amends/return-for-amends.guard';
import { ApplicationTimeframeFormProvider } from './tasks/application-timeframe';
import { dataGapsFormProvider } from './tasks/data-gaps/data-gaps-form.provider';
import { EmissionSourcesFormProvider } from './tasks/emission-sources/emission-sources-form.provider';
import { EmissionsReductionClaimFormProvider } from './tasks/emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { ManagementProceduresFormProvider } from './tasks/management-procedures';
import { MonitoringApproachFormProvider } from './tasks/monitoring-approach';
import { OperatorDetailsFormProvider } from './tasks/operator-details/operator-details-form.provider';
import { VariationDetailsFormProvider } from './tasks/variation-details/variation-details-form.provider';

const canDeactivateEmp: CanDeactivateFn<any> = () => {
  inject(RequestTaskStore).destroyDelegates();
  return true;
};

export const EMP_CHILD_ROUTES: Routes = [
  {
    path: 'flight-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: FlightProceduresFormProvider }],
    loadChildren: () =>
      import('./tasks/flight-procedures/flight-procedures.routes').then((r) => r.EMP_FLIGHT_PROCEDURES_ROUTES),
  },
  {
    path: 'monitoring-approach',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: MonitoringApproachFormProvider }],
    loadChildren: () =>
      import('./tasks/monitoring-approach/monitoring-approach.routes').then((r) => r.EMP_MONITORING_APPROACH_ROUTES),
  },
  {
    path: 'management-procedures',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ManagementProceduresFormProvider }],
    loadChildren: () =>
      import('./tasks/management-procedures/management-procedures.routes').then(
        (r) => r.EMP_MANAGEMENT_PROCEDURES_ROUTES,
      ),
  },
  {
    path: 'abbreviations',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AbbreviationsFormProvider }],
    loadChildren: () => import('../shared/abbreviations/abbreviations.routes').then((r) => r.EMP_ABBREVIATION_ROUTES),
  },
  {
    path: 'service-contact-details',
    loadChildren: () =>
      import('../shared/service-contact-details/service-contact-details.routes').then(
        (r) => r.EMP_SERVICE_CONTACT_DETAILS_ROUTES,
      ),
  },
  {
    path: 'additional-docs',
    providers: [additionalDocumentsFormProvider],
    loadChildren: () =>
      import('../shared/additional-documents/additional-documents.routes').then(
        (r) => r.EMP_ADDITIONAL_DOCUMENTS_ROUTES,
      ),
  },
  {
    path: 'emissions-reduction-claim',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider }],
    loadChildren: () =>
      import('./tasks/emissions-reduction-claim/emissions-reduction-claim.routes').then(
        (r) => r.EMP_EMISSIONS_REDUCTION_CLAIM_ROUTES,
      ),
  },
  {
    path: 'emission-sources',
    providers: [EmissionSourcesFormProvider],
    loadChildren: () =>
      import('./tasks/emission-sources/emission-sources.routes').then((r) => r.EMP_EMISSION_SOURCES_ROUTES),
  },
  {
    path: 'application-timeframe-apply',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ApplicationTimeframeFormProvider }],
    loadChildren: () =>
      import('./tasks/application-timeframe/application-timeframe.routes').then(
        (r) => r.EMP_APPLICATION_TIMEFRAME_ROUTES,
      ),
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
    path: 'data-gaps',
    providers: [dataGapsFormProvider],
    loadChildren: () => import('./tasks/data-gaps/data-gaps.routes').then((r) => r.EMP_DATA_GAPS_ROUTES),
  },
  {
    path: 'submission',
    loadChildren: () => import('../shared/submission/submission.routes').then((r) => r.EMP_SUBMISSION_ROUTES),
  },
  {
    path: 'operator-details',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OperatorDetailsFormProvider }],
    loadChildren: () =>
      import('./tasks/operator-details/operator-details.routes').then((r) => r.EMP_OPERATOR_DETAILS_ROUTES),
  },
  {
    path: 'changes-requested',
    data: { pageTitle: 'Changes requested by the regulator', breadcrumb: true },
    loadComponent: () =>
      import('../../../shared/components/changes-requested/changes-requested.component').then(
        (c) => c.ChangesRequestedComponent,
      ),
  },
];

export const EMP_REVIEW_CHILD_ROUTES: Routes = [
  ...EMP_CHILD_ROUTES,
  {
    path: 'decision',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: OverallDecisionFormProvider }],
    loadChildren: () =>
      import('../shared/overall-decision/overall-decision.routes').then((r) => r.EMP_OVERALL_DECISION_ROUTES),
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
  {
    path: 'notify-operator',
    providers: [PaymentCompletedGuard],
    canActivate: [canActivateEmpNotifyOperator, PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
    loadComponent: () =>
      import('../shared/notify-operator/notify-operator.component').then((c) => c.EmpNotifyOperatorComponent),
  },
];

export const EMP_ROUTES: Routes = [
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
