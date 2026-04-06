import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';

import { RequestActionDTO } from 'pmrv-api';

export const getNerActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  const itemActionTypePipe = new ItemActionTypePipe();

  switch (requestActionType) {
    default:
      return itemActionTypePipe.transform(requestActionType);
  }
};
