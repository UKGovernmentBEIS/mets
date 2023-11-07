import { Routes } from '@angular/router';

import { AircraftTypeComponent } from './aircraft-type.component';

export const AIRCRAFT_TYPE_ROUTES: Routes = [
  {
    path: '',
    component: AircraftTypeComponent,
    children: [
      {
        path: 'search',
        canActivate: [],
        loadComponent: () =>
          import('./search/aircraft-search-page.component').then((c) => c.AircraftSearchPageComponent),
      },
      {
        path: 'add',
        data: { backlink: '../..' },
        canActivate: [],
        loadComponent: () => import('./form/aircraft-type-form.component').then((c) => c.AircraftTypeFormComponent),
      },
      {
        path: 'edit',
        data: { backlink: '../..' },
        canActivate: [],
        loadComponent: () => import('./form/aircraft-type-form.component').then((c) => c.AircraftTypeFormComponent),
      },
      {
        path: 'remove',
        data: { backlink: '../..' },
        canActivate: [],
        loadComponent: () =>
          import('./remove/aircraft-type-remove.component').then((c) => c.AircraftTypeRemoveComponent),
      },
    ],
  },
];
