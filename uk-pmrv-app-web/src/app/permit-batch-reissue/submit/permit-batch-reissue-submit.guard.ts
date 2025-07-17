import { Injectable } from '@angular/core';

import { PermitBatchReissueStore } from './store/permit-batch-reissue.store';

@Injectable({ providedIn: 'root' })
export class PermitBatchReissueSubmitGuard {
  constructor(private readonly store: PermitBatchReissueStore) {}

  canDeactivate(): boolean {
    this.store.reset();
    return true;
  }
}
