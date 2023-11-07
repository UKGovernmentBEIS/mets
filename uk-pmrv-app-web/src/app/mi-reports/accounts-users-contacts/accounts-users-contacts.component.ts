import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { AccountTypePipe } from '@shared/pipes/account-type.pipe';

import { GovukTableColumn } from 'govuk-components';

import { AccountsUsersContactsMiReportResult, AccountUserContact, MiReportsService } from 'pmrv-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { createTableColumns, createTablePage, manipulateResultsAndExportToExcel, pageSize } from '../core/mi-report';
import { AuthorityStatusPipe } from '../pipes/authority-status.pipe';

@Component({
  selector: 'app-accounts-users-contacts',
  template: `
    <app-page-heading size="xl">List of Accounts, Users and Contacts</app-page-heading>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="generateReport()">Execute</button>
      <button appPendingButton govukButton type="button" (click)="exportToExcel()">Export to excel</button>
    </div>
    <div *ngIf="pageItems$ | async as items">
      <ng-container *ngIf="items.length">
        <div class="overflow-auto overflow-auto-table">
          <govuk-table [columns]="tableColumns" [data]="items"></govuk-table>
        </div>
        <app-pagination
          [count]="totalNumOfItems$ | async"
          (currentPageChange)="currentPage$.next($event)"
          [pageSize]="pageSize"
        ></app-pagination>
      </ng-container>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AccountsUsersContactsComponent implements OnInit {
  readonly pageSize = pageSize;
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  accountsUsersContacts$ = this.currentDomain$
    .pipe(
      switchMap((currentDomain) =>
        this.miReportsService.generateReport(currentDomain, { reportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS' }),
      ),
    )
    .pipe(
      map((miReportResult: AccountsUsersContactsMiReportResult) => ({
        ...miReportResult,
        results: this.addPipesToResult(miReportResult.results),
      })),
    );
  reportItems$: Observable<AccountUserContact[]>;
  pageItems$: Observable<AccountUserContact[]>;
  totalNumOfItems$: Observable<number>;

  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();
  tableColumns: GovukTableColumn<AccountUserContact>[];
  domain: string;

  constructor(
    private readonly miReportsService: MiReportsService,
    private readonly backlinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? domain.toLowerCase() : '';
    });
    this.backlinkService.show(this.domain + '/mi-reports');

    this.reportItems$ = this.generateReport$.pipe(
      switchMap(() => this.accountsUsersContacts$),
      tap(
        (miReportResult: AccountsUsersContactsMiReportResult) =>
          (this.tableColumns = createTableColumns(miReportResult.columnNames)),
      ),
      map((miReportResult) => miReportResult.results),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.pageItems$ = combineLatest([this.reportItems$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );
    this.totalNumOfItems$ = this.reportItems$.pipe(map((items) => items.length));
  }

  generateReport() {
    this.generateReport$.next();
    this.router.navigate([], { relativeTo: this.route, queryParams: { page: 1 }, queryParamsHandling: 'merge' });
  }

  addPipesToResult(results: AccountsUsersContactsMiReportResult['results']) {
    return results.map((accountUserContact) => {
      const accountTypePipe = new AccountTypePipe();
      const accountStatusPipe = new AccountStatusPipe();
      const authorityStatusPipe = new AuthorityStatusPipe();

      return {
        ...accountUserContact,
        'Account type': accountTypePipe.transform(accountUserContact['Account type']),
        'Account status': accountStatusPipe.transform(accountUserContact['Account status']),
        'User status': authorityStatusPipe.transform(accountUserContact['User status']),
      };
    });
  }

  exportToExcel() {
    this.accountsUsersContacts$
      .pipe(
        map((miReportResult: ExtendedMiReportResult) =>
          manipulateResultsAndExportToExcel(miReportResult, 'Accounts, Users and Contacts'),
        ),
      )
      .subscribe();
  }
}
