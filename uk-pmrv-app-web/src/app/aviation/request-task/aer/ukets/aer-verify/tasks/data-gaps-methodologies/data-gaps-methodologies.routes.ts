import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { ConservativeMethodComponent } from './conservative-method';
import { canActivateDataGapsMethodologies, canDeactivateDataGapsMethodologies } from './data-gaps-methodologies.guards';
import { MaterialMisstatementComponent } from './material-misstatement';
import { RegulatorApprovedComponent } from './regulator-approved/regulator-approved.component';

export const AER_DATA_GAPS_METHODOLOGIES_ROUTES: Routes = [
  {
    path: '',
    data: { aerTask: 'dataGapsMethodologies' },
    canActivate: [canActivateDataGapsMethodologies],
    canDeactivate: [canDeactivateDataGapsMethodologies],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./data-gaps-methodologies.component').then((c) => c.DataGapsMethodologiesComponent),
      },
      {
        path: 'regulator-approved',
        data: { pageTitle: 'Has the data gap method already been approved by the regulator?', backlink: '../' },
        canActivate: [canActivateTaskForm],
        component: RegulatorApprovedComponent,
      },
      {
        path: 'method-conservative',
        data: { pageTitle: 'Was the method used conservative?', backlink: '../regulator-approved' },
        canActivate: [canActivateTaskForm],
        component: ConservativeMethodComponent,
      },
      {
        path: 'material-misstatement',
        data: { pageTitle: 'Did the method lead to a material misstatement?', backlink: '../method-conservative' },
        canActivate: [canActivateTaskForm],
        component: MaterialMisstatementComponent,
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { pageTitle: 'Check your answers', breadcrumb: 'Methodologies to close data gaps summary' },
        loadComponent: () =>
          import('./methodologies-summary/methodologies-summary.component').then(
            (c) => c.MethodologiesSummaryComponent,
          ),
      },
    ],
  },
];
