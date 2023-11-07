import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import {
  canActivateManagementProceduresCorsia,
  canDeactivateManagementProceduresCorsia,
} from './management-procedures.guards';

export const EMP_MANAGEMENT_PROCEDURES_CORSIA_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateManagementProceduresCorsia],
    canDeactivate: [canDeactivateManagementProceduresCorsia],
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
        path: 'data-management',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-data/management-procedures-data.component').then(
            (c) => c.ManagementProceduresDataComponent,
          ),
      },
      {
        path: 'documentation-record',
        data: { backlink: '../data-management' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './management-procedures-documentation-record/management-procedures-documentation-record.component'
          ).then((c) => c.ManagementProceduresDocumentationRecordComponent),
      },
      {
        path: 'explanation-risks',
        data: { backlink: '../documentation-record' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-explanation-risks/management-procedures-explanation-risks.component').then(
            (c) => c.ManagementProceduresExplanationRisksComponent,
          ),
      },
      {
        path: 'revision-emissions-monitoring-plan',
        data: { backlink: '../explanation-risks' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./management-procedures-revision-emissions/management-procedures-revision-emissions.component').then(
            (c) => c.ManagementProceduresRevisionEmissionsComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Management procedures summary' },
        loadComponent: () =>
          import('./management-procedures-summary/management-procedures-summary.component').then(
            (c) => c.ManagementProceduresSummaryComponent,
          ),
      },
    ],
  },
];
