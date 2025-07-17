import { inject } from '@angular/core';
import { CanActivateFn, Routes } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';

import { RequestActionStore } from '../store';

export const setCommonActionStore: CanActivateFn = () => {
  const requestStore = inject(RequestActionStore);
  const commonStore = inject(CommonActionsStore);

  commonStore.setState({
    action: requestStore.getState().requestActionItem,
    storeInitialized: true,
  });

  return true;
};

export const NON_COMPLIANCE_ROUTES: Routes = [
  {
    path: '',
    canActivate: [setCommonActionStore],
    loadChildren: () =>
      import('./../../../actions/non-compliance/non-compliance.module').then((m) => m.NonComplianceModule),
  },
];
