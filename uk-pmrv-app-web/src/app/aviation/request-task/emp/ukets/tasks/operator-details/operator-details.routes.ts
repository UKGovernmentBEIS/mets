import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateOperatorDetails, canDeactivateOperatorDetails } from './operator-details.guard';

export const EMP_OPERATOR_DETAILS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateOperatorDetails],
    canDeactivate: [canDeactivateOperatorDetails],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-name/operator-details-name.component').then((c) => c.OperatorDetailsNameComponent),
      },
      {
        path: 'flight-identification',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-flight-identification/operator-details-flight-identification.component').then(
            (c) => c.OperatorDetailsFlightIdentificationComponent,
          ),
      },
      {
        path: 'air-operating-certificate',
        data: { backlink: '../flight-identification' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './operator-details-air-operating-certificate/operator-details-air-operating-certificate.component'
          ).then((c) => c.OperatorDetailsAirOperatingCertificateComponent),
      },
      {
        path: 'operating-license',
        data: { backlink: '../air-operating-certificate' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-operating-license/operator-details-operating-license.component').then(
            (c) => c.OperatorDetailsOperatingLicenseComponent,
          ),
      },
      {
        path: 'organisation-structure',
        resolve: {
          backlink: () => {
            return '../operating-licence';
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
        data: { backlink: '../organisation-structure' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./operator-details-activities-description/operator-details-activities-description.component').then(
            (c) => c.OperatorDetailsActivitiesDescriptionComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Operator details summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./operator-details-summary/operator-details-summary.component').then(
            (c) => c.OperatorDetailsSummaryComponent,
          ),
      },
    ],
  },
];
