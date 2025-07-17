import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { RequestActionStore } from '../../store';
import { SHARED_EMP_ROUTES } from '../shared/emp.routes';

const canDeactivateEmp: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

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
        path: 'submitted',
        children: [
          ...SHARED_EMP_ROUTES,
          {
            path: 'operator-details',
            data: { breadcrumb: 'Operator details' },
            loadComponent: () => import('./tasks').then((c) => c.OperatorDetailsComponent),
          },
          {
            path: 'emissions-reduction-claim',
            data: { breadcrumb: 'Emissions reduction claim' },
            loadComponent: () => import('./tasks').then((c) => c.EmissionsReductionClaimComponent),
          },
          {
            path: 'application-timeframe-apply',
            data: { breadcrumb: 'Application timeframe' },
            loadComponent: () => import('./tasks').then((c) => c.ApplicationTimeframeComponent),
          },
        ],
      },
    ],
  },
];
