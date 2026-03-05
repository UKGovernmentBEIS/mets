import { inject } from '@angular/core';
import { ResolveFn, Routes } from '@angular/router';

import { map } from 'rxjs';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { RequestTaskStore } from '../../../../store';
import { monitoringApproachQuery } from '../monitoring-approach/store/monitoring-approach.selectors';
import { canActivateManagementProcedures, canDeactivateManagementProcedures } from './management-procedures.guards';

const resolveEnvironmentalManagementBackLink: ResolveFn<any> = () => {
  return inject(RequestTaskStore).pipe(
    monitoringApproachQuery.selectMonitoringApproach,
    map((approach) => {
      return approach.monitoringApproachType === 'FUEL_USE_MONITORING' ? '../uplift-quantity' : '../risks';
    }),
  );
};

export const EMP_MANAGEMENT_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateManagementProcedures],
    canDeactivate: [canDeactivateManagementProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-roles/management-procedures-roles.component').then(
            (c) => c.ManagementProceduresRolesComponent,
          ),
      },
      {
        path: 'documentation',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-documentation/management-procedures-documentation.component').then(
            (c) => c.ManagementProceduresDocumentationComponent,
          ),
      },
      {
        path: 'responsibilities',
        data: { backlink: '../documentation' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-responsibilities/management-procedures-responsibilities.component').then(
            (c) => c.ManagementProceduresResponsibilitiesComponent,
          ),
      },
      {
        path: 'appropriateness',
        data: { backlink: '../responsibilities' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-appropriateness/management-procedures-appropriateness.component').then(
            (c) => c.ManagementProceduresAppropriatenessComponent,
          ),
      },
      {
        path: 'data-flow',
        data: { pageTitle: 'Data flow activities', backlink: '../appropriateness' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-data-flow/management-procedures-data-flow.component').then(
            (c) => c.ManagementProceduresDataFlowComponent,
          ),
      },
      {
        path: 'quality-assurance',
        data: { backlink: '../data-flow' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-quality-assurance/management-procedures-quality-assurance.component').then(
            (c) => c.ManagementProceduresQualityAssuranceComponent,
          ),
      },
      {
        path: 'data-validation',
        data: { backlink: '../quality-assurance' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-data-validation/management-procedures-data-validation.component').then(
            (c) => c.ManagementProceduresDataValidationComponent,
          ),
      },
      {
        path: 'corrections',
        data: { backlink: '../data-validation' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-corrections/management-procedures-corrections.component').then(
            (c) => c.ManagementProceduresCorrectionsComponent,
          ),
      },
      {
        path: 'outsourced-activities',
        data: { backlink: '../corrections' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './management-procedures-outsourced-activities/management-procedures-outsourced-activities.component'
          ).then((c) => c.ManagementProceduresOutsourcedActivitiesComponent),
      },
      {
        path: 'risks',
        data: { backlink: '../outsourced-activities' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-risks/management-procedures-risks.component').then(
            (c) => c.ManagementProceduresRisksComponent,
          ),
      },
      {
        path: 'risk-assessment',
        data: { backlink: '../risks' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-risk-assessment/management-procedures-risk-assessment.component').then(
            (c) => c.ManagementProceduresRiskAssessmentComponent,
          ),
      },
      {
        path: 'uplift-quantity',
        data: { backlink: '../risk-assessment' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-uplift-quantity/management-procedures-uplift-quantity.component').then(
            (c) => c.ManagementProceduresUpliftQuantityComponent,
          ),
      },
      {
        path: 'environmental-management',
        data: { backlink: ({ backlinkUrl }) => backlinkUrl },
        resolve: { backlinkUrl: resolveEnvironmentalManagementBackLink },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './management-procedures-environmental-management/management-procedures-environmental-management.component'
          ).then((c) => c.ManagementProceduresEnvironmentalManagementComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Management procedures' },
        loadComponent: () =>
          import('./management-procedures-summary/management-procedures-summary.component').then(
            (c) => c.ManagementProceduresSummaryComponent,
          ),
      },
    ],
  },
];
