import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';

import { catchError, defaultIfEmpty, of } from 'rxjs';

import { SettingsFeesPermissionService } from './settings-fees-permission.service';

export function canViewSettingsFees(): CanMatchFn {
  return () =>
    inject(SettingsFeesPermissionService)
      .canView()
      .pipe(
        defaultIfEmpty(false),
        catchError(() => of(false)),
      );
}

export function canExecuteSettingsFees(): CanMatchFn {
  return () =>
    inject(SettingsFeesPermissionService)
      .canExecute()
      .pipe(
        defaultIfEmpty(false),
        catchError(() => of(false)),
      );
}
