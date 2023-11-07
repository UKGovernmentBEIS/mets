import { Routes } from '@angular/router';

import {
  canActivateGeneralInformation,
  canDeactivateGeneralInformation,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/general-information.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_GENERAL_INFORMATION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateGeneralInformation],
    canDeactivate: [canDeactivateGeneralInformation],
    children: [
      {
        path: '',
        data: { pageTitle: 'Verification criteria and operator data' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./general-information.component').then((c) => c.GeneralInformationComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Verification criteria and operator data summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
