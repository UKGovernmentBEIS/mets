import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import {
  canActivateEmissionsReductionClaim,
  canDeactivateEmissionsReductionClaim,
} from './emissions-reduction-claim.guard';

export const AER_CORSIA_EMISSIONS_REDUCTION_CLAIM_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateEmissionsReductionClaim],
    canActivateChild: [canActivateEmissionsReductionClaim],
    canDeactivate: [canDeactivateEmissionsReductionClaim],
    children: [
      {
        path: '',
        data: { pageTitle: 'CORSIA eligible fuels reduction claim' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emissions-reduction-claim-page/emissions-reduction-claim-page.component').then(
            (c) => c.EmissionsReductionClaimPageComponent,
          ),
      },
      {
        path: 'upload-template',
        data: { pageTitle: 'Upload a template and supporting documents', backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./upload-template/upload-template.component').then((c) => c.UploadTemplateComponent),
      },
      {
        path: 'declaration-of-no-double-claiming',
        data: { pageTitle: 'Declaration of no double claiming', backlink: '../upload-template' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./declaration-of-no-double-claiming/declaration-of-no-double-claiming.component').then(
            (c) => c.DeclarationOfNoDoubleClaimingComponent,
          ),
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'CORSIA eligible fuels reduction claim' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./emissions-reduction-claim-summary/emissions-reduction-claim-summary.component').then(
            (c) => c.EmissionsReductionClaimSummaryComponent,
          ),
      },
    ],
  },
];
