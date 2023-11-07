import { InjectionToken } from '@angular/core';

import { PermitApplicationState } from './permit-application.state';
import { PermitApplicationStore } from './permit-application.store';

export const PERMIT_STORE_TOKEN = new InjectionToken<PermitApplicationStore<PermitApplicationState>>(
  'Permit store token',
);
