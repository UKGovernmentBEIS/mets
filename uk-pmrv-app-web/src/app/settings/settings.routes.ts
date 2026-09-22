import { Routes } from '@angular/router';

import { canViewSettingsFees } from './core/settings-fees-permission.guard';
import { SettingsComponent } from './settings.component';

export const SETTINGS_ROUTES: Routes = [
  {
    path: '',
    data: { pageTitle: 'Settings' },
    component: SettingsComponent,
  },
  {
    path: 'fees',
    canMatch: [canViewSettingsFees()],
    data: { pageTitle: 'Settings', breadcrumb: 'Fees' },
    loadChildren: () => import('./fees/fees.routes').then((m) => m.FEES_ROUTES),
  },
];
