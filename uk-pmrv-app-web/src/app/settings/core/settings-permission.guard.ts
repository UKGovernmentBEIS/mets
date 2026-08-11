import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';

import { catchError, defaultIfEmpty, map, of } from 'rxjs';

import { AccountType } from '@core/store';

import { SettingsService } from 'pmrv-api';

export function hasAccessibleSettings(accountType: AccountType): CanMatchFn {
  return () =>
    inject(SettingsService)
      .getAccessibleSections(accountType)
      .pipe(
        map((sections) => sections.length > 0),
        defaultIfEmpty(false),
        catchError(() => of(false)),
      );
}
