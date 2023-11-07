import { Routes } from '@angular/router';

import { canActivateSummaryPage, canActivateTaskForm } from '../../../guards';
import {
  canActivateAggregatedConsumptionFlightData,
  canDeactivateAggregatedConsumptionFlightData,
} from './aggregated-consumption-flight-data.guards';

export const AER_AGGREGATED_CONSUMPTION_FLIGHT_DATA_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateAggregatedConsumptionFlightData],
    canDeactivate: [canDeactivateAggregatedConsumptionFlightData],
    children: [
      {
        path: '',
        canActivate: [canActivateTaskForm],
        loadComponent: () =>
          import('./aggregated-consumption-flight-data-page').then(
            (c) => c.AggregatedConsumptionFlightDataPageComponent,
          ),
      },
      {
        path: 'summary',
        canActivate: [canActivateSummaryPage],
        loadComponent: () =>
          import('./aggregated-consumption-flight-data-summary').then(
            (c) => c.AggregatedConsumptionFlightDataSummaryComponent,
          ),
      },
    ],
  },
];
