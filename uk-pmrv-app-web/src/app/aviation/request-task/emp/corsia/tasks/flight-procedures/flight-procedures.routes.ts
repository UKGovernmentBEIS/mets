import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateFlightProcedures, canDeactivateFlightProcedures } from './flight-procedures.guard';

export const EMP_CORSIA_FLIGHT_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateFlightProcedures],
    canDeactivate: [canDeactivateFlightProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./flight-procedures-aircraft-used/flight-procedures-aircraft-used.component'),
      },
      {
        path: 'completeness-of-the-flights-list',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./flight-procedures-list-flights/flight-procedures-list-flights.component'),
      },
      {
        path: 'list-state-pairs',
        data: { backlink: '../completeness-of-the-flights-list' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./flight-procedures-list-state-pairs/flight-procedures-list-state-pairs.component'),
      },
      {
        path: 'determination-international-flights',
        data: { backlink: '../list-state-pairs' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './flight-procedures-determination-international-flights/flight-procedures-determination-international-flights.component'
          ),
      },
      {
        path: 'determination-international-flights-offset',
        data: { backlink: '../determination-international-flights' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './flight-procedures-determination-international-flights-offset/flight-procedures-determination-international-flights-offset.component'
          ),
      },
      {
        path: 'determination-international-flights-no-monitoring',
        data: { backlink: '../determination-international-flights-offset' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import(
            './flight-procedures-determination-international-flights-no-monitoring/flight-procedures-determination-international-flights-no-monitoring.component'
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Flight and aircraft monitoring procedures summary' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./flight-procedures-summary/flight-procedures-summary.component'),
      },
    ],
  },
];
