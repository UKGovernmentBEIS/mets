import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

import { combineLatest, distinctUntilChanged, filter, map, Observable, switchMap, takeUntil } from 'rxjs';

import { AviationAccountDetailsListComponent } from '@aviation/accounts/components/aviation-account-details-history-list/aviation-account-details-history-list.component';
import {
  AviationAccountsStore,
  initialCurrentAccountState,
  selectAccountDetailsHistory,
  selectAccountDetailsHistoryPage,
  selectAccountDetailsHistoryPageSize,
  selectAccountDetailsHistoryTotal,
} from '@aviation/accounts/store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule, GovukTableColumn } from 'govuk-components';

import { AccountDetailsHistoryDTO, AccountDetailsHistoryService } from 'pmrv-api';

interface ViewModel {
  accountDetailsHistory: AccountDetailsHistoryDTO[];
  total: number;
  page: number;
  pageSize: number;
  params: ParamMap;
  columns: GovukTableColumn[];
}

@Component({
  selector: 'app-aviation-account-details-history',
  imports: [AviationAccountDetailsListComponent, GovukComponentsModule, SharedModule],
  templateUrl: './aviation-account-details-history.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountDetailsHistoryComponent implements OnInit {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(selectAccountDetailsHistory),
    this.store.pipe(selectAccountDetailsHistoryTotal),
    this.store.pipe(selectAccountDetailsHistoryPage),
    this.store.pipe(selectAccountDetailsHistoryPageSize),
    this.route.paramMap,
  ]).pipe(
    map(([accountDetailsHistory, total, page, pageSize, params]) => ({
      accountDetailsHistory,
      total,
      page,
      pageSize,
      params,
      columns: [
        { header: 'Field', field: 'category', widthClass: 'govuk-!-width-one-quarter' },
        { header: 'Previous', field: 'previousValue', widthClass: 'govuk-!-width-one-quarter' },
        { header: 'New', field: 'newValue', widthClass: 'govuk-!-width-one-quarter' },
        { header: 'Reason', field: 'reason', widthClass: 'govuk-!-width-one-quarter' },
      ],
    })),
  );

  constructor(
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly accountDetailsHistoryService: AccountDetailsHistoryService,
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
          return this.accountDetailsHistoryService.getAccountDetailsHistory(
            +params.get('accountId'),
            page - 1,
            pageSize,
          );
        }),
      )
      .subscribe(({ accountDetailsHistoryList, total }) => {
        this.store.setAccountDetailsHistory(accountDetailsHistoryList);
        this.store.setAccountDetailsHistoryTotal(total);
      });

    this.route.queryParamMap
      .pipe(
        map((params) => ({
          page: +params.get('page') || initialCurrentAccountState.accountDetailsHistory.paging.page,
          pageSize: +params.get('pageSize') || initialCurrentAccountState.accountDetailsHistory.paging.pageSize,
        })),
        takeUntil(this.destroy$),
      )
      .subscribe(({ page, pageSize }) => {
        this.store.setAccountDetailsHistoryPaging({ page, pageSize });
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
