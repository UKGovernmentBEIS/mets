import { Injectable } from '@angular/core';

import { Store } from '@core/store/store';

import { initialState, ResetPasswordState } from './reset-password.state';

@Injectable({ providedIn: 'root' })
export class ResetPasswordStore extends Store<ResetPasswordState> {
  constructor() {
    super(initialState);
  }
}
