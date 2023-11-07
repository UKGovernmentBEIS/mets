import { Injectable } from '@angular/core';

import { Store } from '@core/store';

import { EmpBatchReissueState, initialState } from './emp-batch-reissue.state';

@Injectable({ providedIn: 'root' })
export class EmpBatchReissueStore extends Store<EmpBatchReissueState> {
  constructor() {
    super(initialState);
  }

  get state$() {
    return this.asObservable();
  }

  setState(state: EmpBatchReissueState): void {
    super.setState(state);
  }

  patchState(state: Partial<EmpBatchReissueState>) {
    this.setState({ ...this.getState(), ...state });
  }
}
