import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../../guards';
import { canActivateEmissionSources, canDeactivateEmissionSources } from './emission-sources.guard';

export const EMP_EMISSION_SOURCES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateEmissionSources],
    canDeactivate: [canDeactivateEmissionSources],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./emission-sources-page/emission-sources-page.component').then((c) => c.EmissionSourcesPageComponent),
      },
      {
        path: 'aircraft-type',
        canActivate: [canActivateTaskForm],
        loadChildren: () => import('./aircraft-type/aircraft-type.routes').then((c) => c.AIRCRAFT_TYPE_ROUTES),
      },
      {
        path: 'other-fuel',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./other-fuel/aircraft-type-other-fuel.component').then((c) => c.AircraftTypeOtherFuelComponent),
      },
      {
        path: 'multiple-methods',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./multiple-methods/multiple-methods.component').then((c) => c.MultipleMethodsComponent),
      },
      {
        path: 'monitoring-methodology',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./monitoring-methodology/monitoring-methodology.component').then(
            (c) => c.MonitoringMethodologyComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        data: { breadcrumb: 'Emission sources summary' },
        loadComponent: () =>
          import('./emission-sources-summary/emission-sources-summary.component').then(
            (c) => c.EmissionSourcesSummaryComponent,
          ),
      },
    ],
  },
];
