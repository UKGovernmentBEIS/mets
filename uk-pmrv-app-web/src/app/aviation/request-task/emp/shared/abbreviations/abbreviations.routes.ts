import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../guards';
import { canActivateAbbreviations, canDeactivateAbbreviations } from './abbreviations.guards';

export const EMP_ABBREVIATION_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAbbreviations],
    canDeactivate: [canDeactivateAbbreviations],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./abbreviations-page/abbreviations-page.component').then((c) => c.AbbreviationsPageComponent),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Abbreviations' },
        loadComponent: () =>
          import('./abbreviations-summary/abbreviations-summary.component').then(
            (c) => c.AbbreviationsSummaryComponent,
          ),
      },
    ],
  },
];
