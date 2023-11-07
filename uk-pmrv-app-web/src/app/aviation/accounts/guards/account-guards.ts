import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';

export const canActivateEditReportingStatus: CanActivateFn = () => {
  return inject(AuthStore).pipe(
    selectUserRoleType,
    map((role) => role === 'REGULATOR'),
  );
};
