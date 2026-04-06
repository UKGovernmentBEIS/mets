import { Routes } from '@angular/router';

export const NER_ACTION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
  {
    path: 'submitted',
    children: [
      {
        path: '',
        data: { pageTitle: 'New entrant reserve submitted to verifier' },
        loadComponent: () => import('./submitted').then((c) => c.NerSubmittedComponent),
      },
      {
        path: 'details',
        data: { pageTitle: 'Upload new entrant reserve' },
        loadComponent: () => import('./submitted').then((c) => c.NerActionDetailsComponent),
      },
    ],
  },
];
