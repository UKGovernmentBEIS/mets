import { Routes } from '@angular/router';

import {
  canActivateTimeAllocation,
  canDeactivateTimeAllocation,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/time-allocation/time-allocation.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_TIME_ALLOCATION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateTimeAllocation],
    canDeactivate: [canDeactivateTimeAllocation],
    children: [
      {
        path: '',
        data: { pageTitle: 'Time allocation and scope' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./time-allocation.component').then((c) => c.TimeAllocationComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Time allocation and scope summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
