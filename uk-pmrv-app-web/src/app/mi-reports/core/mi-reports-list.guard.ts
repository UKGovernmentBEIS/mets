import { Injectable } from '@angular/core';
import { CanActivate, Resolve } from '@angular/router';

import { map, Observable, switchMap, take, tap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { MiReportSearchResult, MiReportsService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class MiReportsListGuard implements CanActivate, Resolve<MiReportSearchResult[]> {
  miReports: MiReportSearchResult[];

  constructor(private readonly miReportsService: MiReportsService, private readonly authStore: AuthStore) {}

  canActivate(): Observable<boolean> {
    return this.authStore.pipe(selectCurrentDomain).pipe(
      take(1),
      switchMap((currentDomain) => this.miReportsService.getCurrentUserMiReports(currentDomain)),
      tap((result) => (this.miReports = result)),
      map(() => true),
    );
  }

  resolve(): MiReportSearchResult[] {
    return this.miReports;
  }
}
