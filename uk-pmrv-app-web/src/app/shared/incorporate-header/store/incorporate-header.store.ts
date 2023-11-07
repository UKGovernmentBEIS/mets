import { Injectable } from '@angular/core';

import { Store } from '@core/store/store';

import { IncorporateHeaderState, initialState } from './incorporate-header.state';

@Injectable({ providedIn: 'root' })
export class IncorporateHeaderStore extends Store<IncorporateHeaderState> {
  constructor() {
    super(initialState);
  }
  setState(state: IncorporateHeaderState): void {
    super.setState(state);
  }
}
