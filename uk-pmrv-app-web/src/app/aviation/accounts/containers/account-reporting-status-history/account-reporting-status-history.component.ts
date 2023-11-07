import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

import { combineLatest, distinctUntilChanged, filter, map, Observable, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { AviationAccountReportingStatusHistoryDTO, AviationAccountReportingStatusService } from 'pmrv-api';

import {
  AviationAccountsStore,
  initialCurrentAccountState,
  selectPage,
  selectPageSize,
  selectReportingStatusHistory,
  selectTotal,
} from '../../store';

interface ViewModel {
  reportingStatusHistory: AviationAccountReportingStatusHistoryDTO[];
  total: number;
  page: number;
  pageSize: number;
  params: ParamMap;
}

@Component({
  selector: 'app-account-reporting-status-history',
  templateUrl: './account-reporting-status-history.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AccountReportingStatusHistoryComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(selectReportingStatusHistory),
    this.store.pipe(selectTotal),
    this.store.pipe(selectPage),
    this.store.pipe(selectPageSize),
    this.route.paramMap,
  ]).pipe(
    map(([reportingStatusHistory, total, page, pageSize, params]) => ({
      reportingStatusHistory,
      total,
      page,
      pageSize,
      params,
    })),
  );

  constructor(
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly accountReportingService: AviationAccountReportingStatusService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.vm$
      .pipe(
        takeUntil(this.destroy$),
        map(({ page, pageSize, params }) => ({ page, pageSize, params })),
        filter(({ page, pageSize }) => !!page && !!pageSize),
        distinctUntilChanged((previous, current) => {
          return previous.page === current.page && previous.pageSize === current.pageSize;
        }),
        switchMap(({ page, pageSize, params }) => {
          return this.accountReportingService.getReportingStatusHistory(+params.get('accountId'), page - 1, pageSize);
        }),
      )
      .subscribe(({ reportingStatusHistoryList, total }) => {
        this.store.setReportingStatusHistory(reportingStatusHistoryList);
        this.store.setTotal(total);
      });

    this.route.queryParamMap
      .pipe(
        map((params) => ({
          page: +params.get('page') || initialCurrentAccountState.reportingStatusHistory.paging.page,
          pageSize: +params.get('pageSize') || initialCurrentAccountState.reportingStatusHistory.paging.pageSize,
        })),
        takeUntil(this.destroy$),
      )
      .subscribe(({ page, pageSize }) => {
        this.store.setPaging({ page, pageSize });
      });
  }

  onPageChange(page: number) {
    this.router.navigate([], {
      queryParams: { page },
      queryParamsHandling: 'merge',
      relativeTo: this.route,
    });
  }
}
