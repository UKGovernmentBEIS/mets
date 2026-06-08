import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';

import { RequestActionDTO } from 'pmrv-api';

export const getNerActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  const itemActionTypePipe = new ItemActionTypePipe();

  switch (requestActionType) {
    default:
      return itemActionTypePipe.transform(requestActionType);
  }
};

export const nerInputDataResolver: ResolveFn<{
  changesRequired: string;
  actionType: RequestActionDTO['type'];
}> = () => {
  const store = inject(CommonActionsStore);
  const { type, payload } = store.getState().action;

  return { changesRequired: (payload as any).changesRequired, actionType: type };
};
