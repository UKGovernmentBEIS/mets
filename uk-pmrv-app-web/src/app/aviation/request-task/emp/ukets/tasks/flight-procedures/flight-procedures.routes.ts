import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '@aviation/request-task/guards';

import { canActivateFlightProcedures, canDeactivateFlightProcedures } from './flight-procedures.guards';

export const EMP_FLIGHT_PROCEDURES_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateFlightProcedures],
    canDeactivate: [canDeactivateFlightProcedures],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./flight-procedures-aircraft-used').then((c) => c.FlightProceduresAircraftUsedComponent),
      },
      {
        path: 'completeness-of-the-flights-list',
        data: { backlink: '../' },
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./flight-procedures-list-flights').then((c) => c.FlightProceduresListFlightsComponent),
      },
      {
        path: 'flights-covered-by-the-UK-ETS',
        data: { backlink: '../completeness-of-the-flights-list' },
        canActivate: [canActivateTaskForm],
        loadComponent: () => import('./flight-procedures-covered-uk').then((c) => c.FlightProceduresCoveredUkComponent),
      },
      {
        path: 'summary',
        data: { breadcrumb: 'Flight and aircraft monitoring procedures' },
        canActivate: [canActivateSummaryPage],
        loadComponent: () => import('./flight-procedures-summary').then((c) => c.FlightProceduresSummaryComponent),
      },
    ],
  },
];
