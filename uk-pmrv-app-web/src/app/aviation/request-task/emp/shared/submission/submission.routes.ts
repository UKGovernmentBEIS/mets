import { Routes } from '@angular/router';

import { canActivateSubmission, canActivateSubmissionSuccess } from './submission.guards';

export const EMP_SUBMISSION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateSubmission],
    children: [
      {
        path: '',
        loadComponent: () => import('./submission-page').then((c) => c.SubmissionPageComponent),
      },
      {
        path: 'success',
        canActivate: [canActivateSubmissionSuccess],
        loadComponent: () => import('./submission-success').then((c) => c.SubmissionSuccessComponent),
      },
    ],
  },
];
