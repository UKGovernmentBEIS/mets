import { inject } from '@angular/core';
import { CanMatchFn } from '@angular/router';

import { catchError, defaultIfEmpty, of } from 'rxjs';

import { MiReportsUserDefinedService } from 'pmrv-api';

export function canManageCustomReports(): CanMatchFn {
  return () =>
    inject(MiReportsUserDefinedService)
      .hasManageCustomReportsAccess()
      .pipe(
        defaultIfEmpty(false),
        catchError(() => of(false)),
      );
}
