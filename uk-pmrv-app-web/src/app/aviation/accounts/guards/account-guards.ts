import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { map } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { isNil } from 'lodash-es';

import { AviationAccountsStore } from '../store';

export const canActivateEditReportingStatus: CanActivateFn = (route) => {
  const authStore = inject(AuthStore);
  const operatorAccountsStore = inject(AviationAccountsStore);

  const { reportingYear } = route.params;

  return authStore.pipe(
    selectUserRoleType,
    map((role) => role === 'REGULATOR'),
    map((roleCorrect) => {
      if (roleCorrect) {
        const result = operatorAccountsStore
          .getState()
          .currentAccount.reportingStatus?.statuses?.find((repStatus) => repStatus.year === reportingYear);
        operatorAccountsStore.setCurrentStatus(result as any);
      }
      return roleCorrect;
    }),
  );
};

export const canDeactivateEditReportingStatus: CanActivateFn = () => {
  const operatorAccountsStore = inject(AviationAccountsStore);

  operatorAccountsStore.resetEditReportingStatus();

  return true;
};

export const canActivateEditReportingStatusSummary: CanActivateFn = (route) => {
  const operatorAccountsStore = inject(AviationAccountsStore);

  return (
    !isNil(operatorAccountsStore.getState()?.currentAccount?.reportingStatus?.upsertStatus) ||
    createUrlTreeFromSnapshot(route, ['../'])
  );
};

export const canDeactivateFyro: CanActivateFn = () => {
  const operatorAccountsStore = inject(AviationAccountsStore);

  operatorAccountsStore.resetUpsertFyro();

  return true;
};

export const canActivateFyroSummary: CanActivateFn = (route) => {
  const operatorAccountsStore = inject(AviationAccountsStore);

  return (
    !isNil(operatorAccountsStore.getState()?.currentAccount?.upsertFirstYearOfReportingObligation) ||
    createUrlTreeFromSnapshot(route, ['../'])
  );
};
