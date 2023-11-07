import { RequestActionStore } from '../../request-action.store';
import { AccountClosureRequestActionPayload } from '../../request-action.types';

export class AccountClosureActionStoreDelegate {
  constructor(private store: RequestActionStore) {}

  get payload(): AccountClosureRequestActionPayload | null {
    return this.store.getState().requestActionItem?.payload as AccountClosureRequestActionPayload;
  }
}
