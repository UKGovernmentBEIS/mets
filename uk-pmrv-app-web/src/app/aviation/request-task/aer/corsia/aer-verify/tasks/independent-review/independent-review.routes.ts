import { Routes } from '@angular/router';

import {
  canActivateIndependentReview,
  canDeactivateIndependentReview,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review.guard';
import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

export const AER_CORSIA_INDEPENDENT_REVIEW_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateIndependentReview],
    canDeactivate: [canDeactivateIndependentReview],
    children: [
      {
        path: '',
        data: { pageTitle: 'Independent review' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./independent-review.component').then((c) => c.IndependentReviewComponent),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Independent review summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./summary/summary.component').then((c) => c.SummaryComponent),
      },
    ],
  },
];
