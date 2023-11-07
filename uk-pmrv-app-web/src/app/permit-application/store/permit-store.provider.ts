import { Location } from '@angular/common';
import { Router } from '@angular/router';

import { resolveRequestType } from '../../shared/store-resolver/request-type.resolver';
import { StoreContextResolver } from '../../shared/store-resolver/store-context.resolver';
import { PermitApplicationState } from './permit-application.state';
import { PermitApplicationStore } from './permit-application.store';

export function PermitApplicationStoreFactory(storeResolver: StoreContextResolver, location: Location, router: Router) {
  const url = router.getCurrentNavigation()?.finalUrl?.toString() || location.path();
  const requestType = resolveRequestType(url);

  return storeResolver.getStore(requestType) as PermitApplicationStore<PermitApplicationState>;
}
