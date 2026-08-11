import { Routes } from '@angular/router';

import { FeesComponent } from './fees.component';

export const FEES_ROUTES: Routes = [
  {
    path: '',
    component: FeesComponent,
    data: { pageTitle: 'Fees' },
  },
];
