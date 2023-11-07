import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';

import { PermitBatchReissueStore } from './store/permit-batch-reissue.store';

@Injectable({ providedIn: 'root' })
export class PermitBatchReissueSubmitGuard implements CanDeactivate<any> {
  constructor(private readonly store: PermitBatchReissueStore) {}

  canDeactivate(): boolean {
    this.store.reset();
    return true;
  }
}
