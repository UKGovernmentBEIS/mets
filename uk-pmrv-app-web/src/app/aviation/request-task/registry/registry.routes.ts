import { Routes } from '@angular/router';

import { ManualAccountOpeningComponent } from './manual-account-opening/manual-account-opening.component';
import { RegistryConfirmationComponent } from './registry-confirmation/registry-confirmation.component';

export const REGISTRY_TASKS_ROUTES: Routes = [
  {
    path: 'manual-account-opening',
    data: { pageTitle: 'Send information to the Registry' },
    component: ManualAccountOpeningComponent,
  },
  {
    path: 'confirmation',
    data: { pageTitle: 'Information sent to Registry' },
    component: RegistryConfirmationComponent,
  },
];
