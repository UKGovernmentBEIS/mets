import { Injectable } from '@angular/core';

import { Store } from '../../../core/store';
import { initialState, PermitBatchReissueState } from './permit-batch-reissue.state';

@Injectable({ providedIn: 'root' })
export class PermitBatchReissueStore extends Store<PermitBatchReissueState> {
  constructor() {
    super(initialState);
  }

  get state$() {
    return this.asObservable();
  }

  setState(state: PermitBatchReissueState): void {
    super.setState(state);
  }

  patchState(state: Partial<PermitBatchReissueState>) {
    this.setState({ ...this.getState(), ...state });
  }
}
