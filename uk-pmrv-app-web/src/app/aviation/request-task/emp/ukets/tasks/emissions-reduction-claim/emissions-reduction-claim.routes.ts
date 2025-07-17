import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import {
  canActivateEmissionsReductionClaim,
  canDeactivateEmissionsReductionClaim,
} from './emissions-reduction-claim.guards';

export const EMP_EMISSIONS_REDUCTION_CLAIM_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateEmissionsReductionClaim],
    canDeactivate: [canDeactivateEmissionsReductionClaim],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-page/emissions-reduction-claim-page.component').then(
            (c) => c.EmissionsReductionClaimPageComponent,
          ),
      },
      {
        path: 'saf-monitoring-systems-processes',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./saf-monitoring-systems-processes/saf-monitoring-systems-processes.component').then(
            (c) => c.SafMonitoringSystemsProcessesComponent,
          ),
      },
      {
        path: 'rtfo-sustainability-criteria',
        data: { backlink: '../saf-monitoring-systems-processes' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./rtfo-sustainability-criteria/rtfo-sustainability-criteria.component').then(
            (c) => c.RtfoSustainabilityCriteriaComponent,
          ),
      },
      {
        path: 'saf-duplication-prevention',
        data: { backlink: '../rtfo-sustainability-criteria' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./saf-duplication-prevention/saf-duplication-prevention.component').then(
            (c) => c.SafDuplicationPreventionComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Emissions reduction claim' },
        loadComponent: () =>
          import('./emissions-reduction-claim-summary/emissions-reduction-claim-summary.component').then(
            (c) => c.EmissionsReductionClaimSummaryComponent,
          ),
      },
    ],
  },
];
