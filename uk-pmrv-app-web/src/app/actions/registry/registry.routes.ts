import { Routes } from '@angular/router';

import { InformationSentToRegistryComponent } from './information-sent/information-sent.component';

export const REGISTRY_ACTION_ROUTES: Routes = [
  {
    path: 'information-sent',
    data: { pageTitle: 'Information sent to Registry by systen' },
    component: InformationSentToRegistryComponent,
  },
];
