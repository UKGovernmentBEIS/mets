import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateOperatorDetails, canDeactivateOperatorDetails } from './operator-details.guard';

export const AER_OPERATOR_DETAILS_ROUTES: Routes = [
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
        data: { pageTitle: 'Air operating certificate', backlink: '../flight-identification' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './operator-details-air-operating-certificate/operator-details-air-operating-certificate.component'
          ).then((c) => c.OperatorDetailsAirOperatingCertificateComponent),
      },
      {
        path: 'operating-license',
        data: { pageTitle: 'Operating licence', backlink: '../air-operating-certificate' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-operating-license/operator-details-operating-license.component').then(
            (c) => c.OperatorDetailsOperatingLicenseComponent,
          ),
      },
      {
        path: 'organisation-structure',
        data: { pageTitle: 'Organisation structure', backlink: '../operating-license' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-organisation-structure/operator-details-organisation-structure.component').then(
            (c) => c.OperatorDetailsOrganisationStructureComponent,
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
