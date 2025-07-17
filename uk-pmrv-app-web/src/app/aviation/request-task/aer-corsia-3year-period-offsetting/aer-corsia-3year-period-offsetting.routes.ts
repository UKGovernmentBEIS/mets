import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { canActivateSummaryPage, canActivateTaskForm } from '../guards';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import {
  canActivateAerCorsia3YearOffsetting,
  canDeactivateAerCorsia3YearOffsetting,
} from './aer-corsia-3year-period-offsetting.guard';
import { ThreeYearOffsettingRequirementsFormProvider } from './aer-corsia-3year-period-offsetting-form.provider';
import { backlinkResolver } from './util/3year-period-offsetting.util';

export const AER_CORSIA_3YEAR_PERIOD_OFFSETTING_ROUTES: Routes = [
  {
    path: '',
    providers: [{ provide: TASK_FORM_PROVIDER, useClass: ThreeYearOffsettingRequirementsFormProvider }],
    canActivate: [canActivateAerCorsia3YearOffsetting],
    canDeactivate: [canDeactivateAerCorsia3YearOffsetting],
    children: [
      {
        path: '',
        resolve: {
          backlink: backlinkResolver,
        },
        data: { pageTitle: 'Offseting requirements - Add details to calculate the offsetting requirements' },
        loadComponent: () =>
          import('./tasks/offsetting-requirements/offsetting-requirements.component').then(
            (c) => c.ThreeYearOffsettingRequirementsComponent,
          ),
        canActivate: [canActivateTaskForm],
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Offsetting requirements - summary', pageTitle: 'Offseting requirements - Check answers' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./tasks/offsetting-requirements-summary/offsetting-requirements-summary.component').then(
            (c) => c.ThreeYearOffsettingRequirementsSummaryComponent,
          ),
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify Operator of Decision' },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/notify-operator/notify-operator.component').then(
            (c) => c.ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent,
          ),
      },
      {
        path: 'peer-review',
        data: { breadcrumb: 'Send for peer review' },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () =>
          import('./tasks/peer-review/peer-review.component').then(
            (c) => c.AerCorsia3YearOffsettingPeriodOffsettingPeerReviewComponent,
          ),
      },
    ],
  },
];
