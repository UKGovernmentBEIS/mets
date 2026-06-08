import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap, take, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { UserContactPipe } from '@shared/pipes/user-contact.pipe';

import { GovukTableColumn } from 'govuk-components';

import { MiReportsService, UserReportEntry, UsersMiReportResult } from 'pmrv-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { createTableColumns, createTablePage, manipulateResultsAndExportToExcel, pageSize } from '../core/mi-report';
import { UserRoleAllTypesPipe } from '../pipes/user-role-all-types.pipe';

@Component({
  selector: 'app-users-for-service-authority',
  standalone: false,
  template: `
    <app-page-heading size="xl">List of users</app-page-heading>
    <div class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="generateReport()">Execute</button>
      <button appPendingButton govukButton type="button" (click)="exportToExcel()">Export to excel</button>
    </div>
    <div *ngIf="pageItems$ | async as items">
      <ng-container *ngIf="items.length; else noResults">
        <div class="overflow-auto overflow-auto-table">
          <govuk-table [columns]="tableColumns" [data]="items"></govuk-table>
        </div>
        <app-pagination
          [count]="totalNumOfItems$ | async"
          (currentPageChange)="currentPage$.next($event)"
          [pageSize]="pageSize"></app-pagination>
      </ng-container>
      <ng-template #noResults>
        <div class="govuk-body"><h2>No results</h2></div>
      </ng-template>
    </div>
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsersForServiceAuthorityComponent implements OnInit {
  readonly pageSize = pageSize;
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, take(1));

  listOfUserReportEntries$ = this.currentDomain$
    .pipe(
      switchMap((currentDomain) =>
        this.miReportsService.generateReport(currentDomain, {
          reportType: 'LIST_OF_USER_REPORT_ENTRIES',
        }),
      ),
    )
    .pipe(
      map((miReportResult: UsersMiReportResult) => ({
        ...miReportResult,
        results: this.addPipesToResult(miReportResult.results),
      })),
    );

  reportItems$: Observable<UserReportEntry[]>;
  pageItems$: Observable<UserReportEntry[]>;
  totalNumOfItems$: Observable<number>;
  domain: string;

  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();

  tableColumns: GovukTableColumn<UserReportEntry>[];

  constructor(
    private readonly miReportsService: MiReportsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? domain.toLowerCase() : '';
    });

    this.reportItems$ = this.generateReport$.pipe(
      switchMap(() => this.listOfUserReportEntries$),
      tap((miReportResult) => (this.tableColumns = createTableColumns(miReportResult.columnNames))),
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

  exportToExcel() {
    this.listOfUserReportEntries$
      .pipe(
        map((miReportResult: ExtendedMiReportResult) =>
          manipulateResultsAndExportToExcel(miReportResult, 'List of users'),
        ),
      )
      .subscribe();
  }

  addPipesToResult(results: UsersMiReportResult['results']) {
    return results.map((account) => {
      const capitalizeFirstPipe = new CapitalizeFirstPipe();
      const userRolePipe = new UserRoleAllTypesPipe();
      const userContactPipe = new UserContactPipe();

      return {
        ...account,
        'User Account status': capitalizeFirstPipe.transform(account['User Account status']),
        'User type': userRolePipe.transform(account['User type']),
        'Contact types': account['Contact types']
          ?.map((contactType) => userContactPipe.transform(contactType)?.split(' ')?.[0])
          ?.join(', '),
      };
    });
  }
}
