import { Routes } from '@angular/router';

import { canActivateSummaryPage } from '@aviation/request-task/guards';

import {
  canActivateOverallDecision,
  canActivateOverallDecisionForms,
  canActivateOverallDecisionReason,
  canDeactivateOverallDecision,
} from './overall-decision.guard';

export const EMP_OVERALL_DECISION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateOverallDecision],
    canDeactivate: [canDeactivateOverallDecision],
    children: [
      {
        path: '',
        canActivate: [canActivateOverallDecisionForms],
        loadComponent: () =>
          import('./overall-decision-action/overall-decision-action.component').then(
            (c) => c.OverallDecisionActionComponent,
          ),
      },
      {
        path: 'reason',
        canActivate: [canActivateOverallDecisionReason, canActivateOverallDecisionForms],
        loadComponent: () =>
          import('./overall-decision-reason/overall-decision-reason.component').then(
            (c) => c.OverallDecisionReasonComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateOverallDecisionReason, canActivateSummaryPage],
        loadComponent: () =>
          import('./overall-decision-summary/overall-decision-summary.component').then(
            (c) => c.OverallDecisionSummaryComponent,
          ),
      },
    ],
  },
];
