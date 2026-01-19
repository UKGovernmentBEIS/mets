import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { AviationAccountEmpDTO, AviationAccountReportingStatusService, AviationAccountViewService } from 'pmrv-api';

import { AviationAccountsStore, selectReportingStatus } from '../store';

@Injectable({
  providedIn: 'root',
})
export class AviationAccountGuard {
  account: AviationAccountEmpDTO;

  constructor(
    private readonly accountViewService: AviationAccountViewService,
    private readonly store: AviationAccountsStore,
    private readonly reportingStatusService: AviationAccountReportingStatusService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.store
      .pipe(selectReportingStatus, first())
      .pipe(
        switchMap((reportingStatus) => {
          return combineLatest([
            this.accountViewService.getAviationAccountById(Number(route.paramMap.get('accountId'))),
            this.reportingStatusService.getAllReportingStatuses(
              Number(route.paramMap.get('accountId')),
              reportingStatus.paging.page - 1,
              reportingStatus.paging.pageSize,
            ),
          ]);
        }),
      )
      .pipe(
        tap(([account, reportingStatuses]) => {
          this.store.setCurrentAccount(account);
          this.store.setReportingStatuses((reportingStatuses as any)?.reportingStatusList);
          this.store.setReportingStatusTotal((reportingStatuses as any)?.total);
        }),
        map(([account]) => !!account),
      );
  }
}
