import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

export const revocationAllowancesBacklinkResolver: ResolveFn<string> = () => {
  const store = inject(PermitRevocationStore);

  return store.isFinalAlrVisible$.pipe(map((isFinalAlrVisible) => (isFinalAlrVisible ? '../final-alr' : '../report')));
};
