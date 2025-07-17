import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateOperatorDetails, canDeactivateOperatorDetails } from './operator-details.guard';

export const EMP_OPERATOR_DETAILS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateOperatorDetails],
    canDeactivate: [canDeactivateOperatorDetails],
    children: [
      {
        path: '',
        data: { pageTitle: 'Operator details' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-name/operator-details-name.component').then((c) => c.OperatorDetailsNameComponent),
      },
      {
        path: 'flight-identification',
        data: { pageTitle: 'Flight identification', backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-flight-identification/operator-details-flight-identification.component').then(
            (c) => c.OperatorDetailsFlightIdentificationComponent,
          ),
      },
      {
        path: 'air-operating-certificate',
        data: { pageTitle: 'Air Operating Certificate', backlink: '../flight-identification' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './operator-details-air-operating-certificate/operator-details-air-operating-certificate.component'
          ).then((c) => c.OperatorDetailsAirOperatingCertificateComponent),
      },
      {
        path: 'organisation-structure',
        data: { pageTitle: 'Organisation structure' },
        resolve: {
          backlink: () => {
            return '../air-operating-certificate';
          },
        },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-organisation-structure/operator-details-organisation-structure.component').then(
            (c) => c.OperatorDetailsOrganisationStructureComponent,
          ),
      },
      {
        path: 'activities-description',
        data: { pageTitle: 'Description of your activities', backlink: '../organisation-structure' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-activities-description/operator-details-activities-description.component').then(
            (c) => c.OperatorDetailsActivitiesDescriptionComponent,
          ),
      },
      {
        path: 'subsidiary-companies',
        data: { pageTitle: 'Subsidiary companies', backlink: '../activities-description' },
        canActivate: [canActivateTaskForm],
        loadChildren: () =>
          import('./operator-details-subsidiary-companies/subsidiary-companies.routes').then(
            (r) => r.SUBSIDIARY_COMPANY_ROUTES,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Operator details' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./operator-details-summary/operator-details-summary.component').then(
            (c) => c.OperatorDetailsSummaryComponent,
          ),
      },
    ],
  },
];
