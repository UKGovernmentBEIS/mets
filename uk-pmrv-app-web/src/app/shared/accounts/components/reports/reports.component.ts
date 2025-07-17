import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  combineLatest,
  distinctUntilChanged,
  iif,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { AviationAccountsStore } from '@aviation/accounts/store';
import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';

import {
  AccountReportingService,
  AviationAccountReportingService,
  RequestDetailsDTO,
  RequestSearchCriteria,
  RequestsService,
} from 'pmrv-api';

import { BaseComponent, Reports } from '../shared/base/base.component';
import { reportsStatusesMap, reportsStatusesTagMap, reportsTypesMap, reportsTypesTagsMap } from './reportsMap';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styles: `
    span.search-results-list_item_status {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReportsComponent extends BaseComponent implements OnInit {
  currentPageData$: Observable<Reports[]>;
  data$: Observable<Reports[]>;
  category: RequestSearchCriteria['category'] = 'REPORTING';

  reportsTypesMap: Record<string, string[]>;
  reportsTypesTagsMap = reportsTypesTagsMap;
  reportsStatusesMap: Record<string, string>;
  reportsStatusesTagMap = reportsStatusesTagMap;

  private readonly configFeatures$ = this.configStore.asObservable().pipe(map((state) => state.features));

  constructor(
    fb: UntypedFormBuilder,
    authStore: AuthStore,
    route: ActivatedRoute,
    destroy$: DestroySubject,
    requestsService: RequestsService,
    store: AviationAccountsStore,
    private readonly accountReportingService: AccountReportingService,
    private readonly aviationAccountReportingService: AviationAccountReportingService,
    private readonly configStore: ConfigStore,
  ) {
    super(fb, authStore, route, destroy$, requestsService, store);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.configFeatures$
      .pipe(
        takeUntil(this.destroy$),
        tap((features) => {
          const corsia3yearOffsettingEnabled = features.corsia3yearOffsettingEnabled;
          const bdrEnabled = features.bdrEnabled;

          this.reportsTypesMap = reportsTypesMap[this.domain];

          if (!corsia3yearOffsettingEnabled) {
            delete this.reportsTypesMap['Calculate 3-year offsetting requirements'];
          }

          if (!bdrEnabled) {
            delete this.reportsTypesMap['Baseline data report'];
          }
        }),
      )
      .subscribe();

    this.reportsStatusesMap = reportsStatusesMap[this.domain];

    this.data$ = this.getRequestDetails().pipe(
      map((results) => {
        const totalData = this.buildTotalData(results.requestDetails);
        this.totalDataNumber$.next(totalData.length);
        this.showPagination$.next(totalData.length > this.itemsPerPage);

        return totalData;
      }),
      withLatestFrom(this.accountId$),
      switchMap(([data, accountId]) => {
        return iif(() => data.length === 0, of([]), this.getReportableEmissions(accountId, data));
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.currentPageData$ = combineLatest([this.data$, this.page$.pipe(distinctUntilChanged())]).pipe(
      map(([totalData, page]) => this.getCurrentPageData(totalData, page - 1, this.itemsPerPage)),
    );
  }

  private getCurrentPageData(data: Reports[], page: number, pageSize: number): Reports[] {
    return data.slice(page * pageSize, (page + 1) * pageSize);
  }

  private buildTotalData(data: RequestDetailsDTO[]): Reports[] {
    const detailsByYear = this.groupDetailsByYear(data);

    return (
      (detailsByYear &&
        Object.keys(detailsByYear)
          .map((year) => ({
            year: year,
            reportableEmissions: 0,
            details: detailsByYear[year].sort(
              (a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime(),
            ),
            hasEmissionReportNotRequired: this.getHasEmissionReportNotRequiredValue(
              detailsByYear[year].sort(
                (a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime(),
              ),
            ),
            hasDoeCorsia: this.getHasDoeCorsia(detailsByYear[year]),
          }))
          .sort((a, b) => Number(b.year) - Number(a.year))) ??
      []
    );
  }

  private getHasEmissionReportNotRequiredValue(detailsByYear): boolean {
    return !!detailsByYear
      .filter(
        (details) =>
          details.requestType === 'AVIATION_AER_CORSIA' ||
          details.requestType === 'AER' ||
          details.requestType === 'AVIATION_AER_UKETS',
      )
      .find((x) => x.requestStatus === 'NOT_REQUIRED');
  }

  private getHasDoeCorsia(detailsByYear): boolean {
    return detailsByYear.filter((details) => details.requestType === 'AVIATION_DOE_CORSIA').length > 0;
  }

  private getReportableEmissions(accountId: number, reports: Reports[]) {
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
                  report.details?.filter(
                    (el) =>
                      [
                        'AVIATION_AER_UKETS',
                        'AVIATION_DRE_UKETS',
                        'AVIATION_AER_CORSIA',
                        'AVIATION_DOE_CORSIA',
                      ].includes(el.requestType) && el.requestMetadata['exempted'],
                  ).length > 0,
              };
            });
          }),
        );
    }
  }
}
