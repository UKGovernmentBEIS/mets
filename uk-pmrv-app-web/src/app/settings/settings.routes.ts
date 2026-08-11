import { Routes } from '@angular/router';

import { SettingsComponent } from './settings.component';

export const SETTINGS_ROUTES: Routes = [
  {
    path: '',
    data: { pageTitle: 'Settings' },
    component: SettingsComponent,
  },
  {
    path: 'fees',
    data: { pageTitle: 'Settings', breadcrumb: 'Fees' },
    loadChildren: () => import('./fees/fees.routes').then((m) => m.FEES_ROUTES),
  },
];
