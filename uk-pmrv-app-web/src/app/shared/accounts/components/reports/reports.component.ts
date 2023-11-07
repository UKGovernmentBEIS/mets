import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  filter,
  iif,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  takeUntil,
  withLatestFrom,
} from 'rxjs';

import { AviationAccountDetails, AviationAccountsStore, selectAccountInfo } from '@aviation/accounts/store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AccountType, AuthStore, selectCurrentDomain } from '@core/store/auth';
import { originalOrder } from '@shared/keyvalue-order';

import {
  AccountReportingService,
  AviationAccountReportingService,
  InstallationAccountPermitDTO,
  RequestDetailsDTO,
  RequestSearchByAccountCriteria,
  RequestsService,
} from 'pmrv-api';

import { reportsStatusesMap, reportsStatusesTagMap, reportsTypesMap, reportsTypesTagsMap } from './reportsMap';

interface Report {
  year: string;
  reportableEmissions: number;
  reportableOffsetEmissions?: number;
  reportableReductionClaimEmissions?: number;
  reportsDetails: RequestDetailsDTO[];
  isExempt?: boolean;
}

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styles: [
    `
      span.search-results-list_item_status {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReportsComponent implements OnInit {
  reportsTypesMap = reportsTypesMap;
  reportsTypesTagsMap = reportsTypesTagsMap;
  reportsStatusesMap = reportsStatusesMap;
  reportsStatusesTagMap = reportsStatusesTagMap;

  readonly originalOrder = originalOrder;
  readonly pageSize = 5;
  page$ = new BehaviorSubject<number>(1);
  showPagination$ = new BehaviorSubject<boolean>(true);
  totalReportsNumber$ = new BehaviorSubject<number>(0);

  reportTypesPerDomain: Record<string, string[]>;
  reportStatusesPerDomain: Record<string, string>;
  domain: AccountType;
  accountId$: Observable<number>;
  reports$: Observable<Report[]>;
  allReports$: Observable<Report[]>;

  searchForm: UntypedFormGroup = this.fb.group({
    reportsTypes: [[], { updateOn: 'change' }],
    reportsStatuses: [[], { updateOn: 'change' }],
  });

  private searchTypes$ = new BehaviorSubject<RequestDetailsDTO['requestType'][]>([]);
  private searchStatuses$ = new BehaviorSubject<RequestSearchByAccountCriteria['requestStatuses'][]>([]);
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    private readonly requestsService: RequestsService,
    private readonly authStore: AuthStore,
    private readonly accountReportingService: AccountReportingService,
    private readonly aviationAccountReportingService: AviationAccountReportingService,
    private readonly destroy$: DestroySubject,
    private readonly store: AviationAccountsStore,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain;
    });

    if (this.domain === 'INSTALLATION') {
      this.accountId$ = (
        this.route.data as Observable<{
          accountPermit: InstallationAccountPermitDTO;
        }>
      ).pipe(map((data) => data.accountPermit?.account.id));
    } else {
      this.accountId$ = this.store.pipe(selectAccountInfo).pipe(
        filter((account) => !!account),
        map((account: AviationAccountDetails) => account.id),
      );
    }

    this.reportTypesPerDomain = reportsTypesMap[this.domain];
    this.reportStatusesPerDomain = reportsStatusesMap[this.domain];
    this.searchForm
      .get('reportsTypes')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchTypes$.next(this.searchForm.get('reportsTypes').value));

    this.searchForm
      .get('reportsStatuses')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchStatuses$.next(this.searchForm.get('reportsStatuses').value));

    this.allReports$ = combineLatest([this.accountId$, this.searchTypes$, this.searchStatuses$]).pipe(
      switchMap(([accountId, types, statuses]) =>
        this.requestsService.getRequestDetailsByAccountId({
          accountId: accountId,
          category: 'REPORTING',
          requestTypes: types.reduce((acc, val) => acc.concat(val), []),
          requestStatuses: statuses.reduce((acc, val) => acc.concat(val), []),
          pageNumber: 0,
          pageSize: 999999,
        }),
      ),
      map((results) => {
        const totalReports = this.buildTotalReportsData(results.requestDetails);

        this.totalReportsNumber$.next(totalReports.length);
        this.showPagination$.next(totalReports.length > this.pageSize);

        return totalReports;
      }),
      withLatestFrom(this.accountId$),
      switchMap(([reports, accountId]) => {
        return iif(() => reports.length === 0, of([]), this.getEmissions(accountId, reports));
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.reports$ = combineLatest([this.allReports$, this.page$.pipe(distinctUntilChanged())]).pipe(
      map(([totalReports, page]) => this.getCurrentPageReports(totalReports, page - 1, this.pageSize)),
    );
  }

  private getCurrentPageReports(reports: Report[], page: number, pageSize: number): Report[] {
    return reports.slice(page * pageSize, (page + 1) * pageSize);
  }

  private buildTotalReportsData(reports: RequestDetailsDTO[]): Report[] {
    const reportsDetailsByYear = reports.reduce((acc, val) => {
      const year = (val.requestMetadata as any).year as number;
      const yearReportsDetails = acc[year] || [];

      return {
        ...acc,
        [year]: [...yearReportsDetails, val],
      };
    }, {});

    return (
      (reportsDetailsByYear &&
        Object.keys(reportsDetailsByYear)
          .map((year) => ({
            year: year,
            reportableEmissions: 0,
            reportsDetails: reportsDetailsByYear[year].sort(
              (a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime(),
            ),
          }))
          .sort((a, b) => Number(b.year) - Number(a.year))) ??
      []
    );
  }

  private getEmissions(accountId: number, reports: Report[]) {
    if (this.domain === 'INSTALLATION') {
      return this.accountReportingService
        .getReportableEmissions(accountId, {
          years: reports.map((report) => +report.year),
        })
        .pipe(
          map((reportableEmissions) => {
            return reports.map((report) => {
              return {
                ...report,
                reportableEmissions: reportableEmissions[report.year],
              };
            });
          }),
        );
    } else {
      return this.aviationAccountReportingService
        .getAviationAccountReportableEmissions(accountId, {
          years: reports.map((report) => +report.year),
        })
        .pipe(
          map((reportableEmissions) => {
            return reports.map((report) => {
              return {
                ...report,
                reportableEmissions: reportableEmissions?.[report.year]?.reportableEmissions,
                reportableOffsetEmissions: (reportableEmissions?.[report.year] as any)?.reportableOffsetEmissions,
                reportableReductionClaimEmissions: (reportableEmissions?.[report.year] as any)
                  ?.reportableReductionClaimEmissions,
                isExempt:
                  reportableEmissions?.[report.year]?.exempted ||
                  report.reportsDetails?.filter(
                    (el) =>
                      ['AVIATION_AER_UKETS', 'AVIATION_DRE_UKETS', 'AVIATION_AER_CORSIA'].includes(el.requestType) &&
                      el.requestMetadata['exempted'],
                  ).length > 0,
              };
            });
          }),
        );
    }
  }
}
