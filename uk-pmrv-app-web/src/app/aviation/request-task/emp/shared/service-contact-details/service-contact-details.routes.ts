import { Routes } from '@angular/router';

export const EMP_SERVICE_CONTACT_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { breadcrumb: 'Service contact details' },
        loadComponent: () => import('./service-contact-details-page/service-contact-details-page.component'),
      },
    ],
  },
];
