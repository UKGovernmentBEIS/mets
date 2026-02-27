import { ChangeDetectionStrategy, Component, computed, inject, input, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { combineLatest, filter, switchMap, take, tap } from 'rxjs';

import { Paging } from '@shared/model';

import { GovukTableColumn } from 'govuk-components';

import { AviationAccountReportingStatusService } from 'pmrv-api';

import {
  AviationAccountsStore,
  selectAccountInfo,
  selectReportingStatus,
  selectReportingStatusesPaging,
} from '../../store';
import { ACCOUNT_REPORTING_STATUS_COLUMNS } from './account-reporting-status-details';

interface ViewModel {
  columns: Array<GovukTableColumn>;
  data: Array<unknown>;
  total: number;
  paging: Paging;
}

@Component({
  selector: 'app-account-reporting-status',
  standalone: false,
  templateUrl: './account-reporting-status.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountReportingStatusComponent {
  private readonly aviationAccountsStore: AviationAccountsStore = inject(AviationAccountsStore);
  private readonly reportingStatusService: AviationAccountReportingStatusService = inject(
    AviationAccountReportingStatusService,
  );

  private readonly reportingStatus = toSignal(this.aviationAccountsStore.pipe(selectReportingStatus));
  public readonly editable = input<boolean>(true);
  public readonly vm: Signal<ViewModel> = computed(() => {
    const { total, paging, statuses } = this.reportingStatus();
    return {
      columns: this.editable()
        ? ACCOUNT_REPORTING_STATUS_COLUMNS
        : ACCOUNT_REPORTING_STATUS_COLUMNS.filter((x) => x.field !== 'actions'),
      data: statuses,
      total,
      paging,
    };
  });

  public onPageChange(page: number) {
    combineLatest([
      this.aviationAccountsStore.pipe(selectAccountInfo),
      this.aviationAccountsStore.pipe(selectReportingStatusesPaging),
    ])
      .pipe(
        take(1),
        filter(([, paging]) => {
          return paging.page !== page;
        }),
        tap(() => this.aviationAccountsStore.setReportingStatusCurrentPage(page)),
        switchMap(([account, paging]) =>
          this.reportingStatusService.getAllReportingStatuses(account.id, page - 1, paging.pageSize),
        ),
      )
      .subscribe((reportingStatuses) => {
        this.aviationAccountsStore.setReportingStatuses((reportingStatuses as any)?.reportingStatusList);
        this.aviationAccountsStore.setReportingStatusTotal((reportingStatuses as any)?.total);
      });
  }
}
