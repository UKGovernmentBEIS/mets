import { InjectionToken } from '@angular/core';

import { AccountType } from '../store/auth';

export const ACCOUNT_TYPE = new InjectionToken<AccountType>('Account type', {
  factory: () => 'INSTALLATION',
});
