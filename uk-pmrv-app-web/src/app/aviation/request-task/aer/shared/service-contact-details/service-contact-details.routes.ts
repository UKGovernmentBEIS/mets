import { Routes } from '@angular/router';

export const AER_SERVICE_CONTACT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { pageTitle: 'Service contact details', breadcrumb: true },
        loadComponent: () => import('./service-contact-details-page/service-contact-details-page.component'),
      },
    ],
  },
];
