import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { canActivateSummaryPage, canActivateTaskForm } from '../guards';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import {
  canActivateAerCorsiaAnnualOffsetting,
  canDeactivateAerCorsiaAnnualOffsetting,
} from './aer-corsia-annual-offsetting.guard';
import { AnnualOffsettingRequirementsFormProvider } from './aer-corsia-annual-offsetting-form.provider';
import { backlinkResolver } from './util/annual-offsetting.util';

export const AER_CORSIA_ANNUAL_OFFSETTING_ROUTES: Routes = [
  {
    path: '',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: AnnualOffsettingRequirementsFormProvider }],
    canActivate: [canActivateAerCorsiaAnnualOffsetting],
    canDeactivate: [canDeactivateAerCorsiaAnnualOffsetting],
    children: [
      {
        path: '',
        resolve: {
          backlink: backlinkResolver,
        },
        canActivate: [canActivateTaskForm],
        data: {
          pageTitle:
            'Offseting Requirements - Input total Chapter 3 State emissions and sector growth value to calculate the annual offsetting requirements',
        },
        loadComponent: () =>
          import('./tasks/offsetting-requirements/offsetting-requirements.component').then(
            (c) => c.AnnualOffsettingRequirementsComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Offsetting requirements - summary', pageTitle: 'Offsetting requirements - Check answers' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./tasks/offsetting-requirements-summary/offsetting-requirements-summary.component').then(
            (c) => c.AnnualOffsettingRequirementsSummaryComponent,
          ),
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify Operator of Decision' },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/notify-operator/notify-operator.component').then(
            (c) => c.AnnualOffsettingRequirementNotifyOperatorComponent,
          ),
      },
      {
        path: 'peer-review',
        data: { breadcrumb: 'Send for peer review' },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('@aviation/request-task/aer-corsia-annual-offsetting/tasks/peer-review/peer-review.component').then(
            (c) => c.AerCorsiaAnnualOffsettingPeerReviewComponent,
          ),
      },
    ],
  },
];
