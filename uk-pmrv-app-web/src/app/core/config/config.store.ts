import { Injectable } from '@angular/core';

import { Store } from '@core/store';

import { ConfigState, initialState } from './config.state';

@Injectable({ providedIn: 'root' })
export class ConfigStore extends Store<ConfigState> {
  constructor() {
    super(initialState);
  }
}
