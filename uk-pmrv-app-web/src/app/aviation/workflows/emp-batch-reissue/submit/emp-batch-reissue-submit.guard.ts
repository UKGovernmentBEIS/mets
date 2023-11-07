import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';

import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';

@Injectable({ providedIn: 'root' })
export class EmpBatchReissueSubmitGuard implements CanDeactivate<any> {
  constructor(private readonly store: EmpBatchReissueStore) {}

  canDeactivate(): boolean {
    this.store.reset();
    return true;
  }
}
