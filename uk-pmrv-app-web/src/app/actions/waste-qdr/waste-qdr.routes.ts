import { Routes } from '@angular/router';

import { WasteQdrActionReturnedForAmendsComponent, WasteQdrSubmittedComponent } from '.';

export const WASTE_QDR_ACTION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
  {
    path: 'submitted',
    data: { pageTitle: 'Quarterly data report submitted to regulator' },
    component: WasteQdrSubmittedComponent,
  },
  {
    path: 'returned-for-amends',
    data: { pageTitle: 'Quarterly data report returned for amends' },
    component: WasteQdrActionReturnedForAmendsComponent,
  },
];
