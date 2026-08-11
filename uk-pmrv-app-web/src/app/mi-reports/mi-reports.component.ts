import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  combineLatest,
  debounceTime,
  defer,
  delay,
  distinctUntilChanged,
  map,
  Observable,
  of,
  shareReplay,
  skip,
  startWith,
  switchMap,
  take,
} from 'rxjs';

import { ConfigService } from '@core/config/config.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { GovukTableColumn } from 'govuk-components';

import { MiReportsUserDefinedService, MiReportSystemSearchResult } from 'pmrv-api';

import { getCategoryOptions } from './core/custom-report';
import { miReportTypeDescriptionMap, miReportTypeLinkMap } from './core/mi-report';
import {
  MiReportsStore,
  selectPage,
  selectSearchTerm,
  selectSelectedCategory,
  selectShowFavouritesOnly,
  selectTotal,
} from './store';

// the API only accepts search terms of at least 3 characters
const searchableTerm = (term: string): string => (term.trim().length >= 3 ? term.trim() : '');

@Component({
  selector: 'app-mi-reports',
  standalone: false,
  templateUrl: './mi-reports.component.html',
  styleUrl: './mi-reports.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  private readonly configService = inject(ConfigService);
  private readonly store = inject(MiReportsStore);
  private readonly authStore = inject(AuthStore);

  private readonly currentDomain$ = this.authStore.pipe(
    selectCurrentDomain,
    switchMap((domain) => (domain ? [domain] : [])),
    take(1),
  );

  readonly reportingImprovementsEnabled$ = this.configService.isFeatureEnabled('reportingImprovementsEnabled');
  readonly canManageCustomReports$ = this.miReportsService.hasManageCustomReportsAccess();
  readonly notification = this.router.currentNavigation()?.extras.state?.notification;
  private readonly standardReportsData$: Observable<MiReportSystemSearchResult[]> = this.route.data.pipe(
    map((data) => data.standardReports),
  );

  readonly pageSize = 10;
  readonly miReportTypeLinkMap = miReportTypeLinkMap;
  readonly miReportTypeDescriptionMap = miReportTypeDescriptionMap;

  readonly searchTerm$ = this.store.pipe(selectSearchTerm);
  readonly selectedCategory$ = this.store.pipe(selectSelectedCategory);
  readonly showFavouritesOnly$ = this.store.pipe(selectShowFavouritesOnly);
  readonly total$ = this.store.pipe(selectTotal);
  customTableColumns: GovukTableColumn[] = [
    { field: 'reportName', header: 'Report name', widthClass: 'govuk-!-width-one-third' },
    { field: 'categories', header: 'Category', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'description', header: 'Description' },
  ];

  readonly categoryOptions$ = getCategoryOptions(this.miReportsService);

  onCategoryChange(categoryId: number | ''): void {
    this.store.setSelectedCategory(categoryId);
  }

  onSearchTermChange(term: string): void {
    this.store.setSearchTerm(term);
  }

  onShowFavouritesOnlyChange(showFavouritesOnly: boolean): void {
    this.store.setShowFavouritesOnly(showFavouritesOnly);
  }

  onPageChange(page: number): void {
    this.store.setPage(page);
  }

  standardCurrentPageData$ = combineLatest([this.standardReportsData$, this.reportingImprovementsEnabled$]).pipe(
    map(([reportsData, reportingImprovementsEnabled]) =>
      (reportingImprovementsEnabled ? reportsData : [...reportsData, { miReportType: 'CUSTOM' }])
        .map((data) => ({ ...data, link: miReportTypeLinkMap[data.miReportType] }))
        .sort((a, b) => a.miReportType.localeCompare(b.miReportType)),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  // debounce only the typing: skip the current store value and re-emit it via startWith,
  // so the initial load (including a term restored from the store) happens immediately
  // instead of after the debounce delay
  private readonly debouncedSearchTerm$ = defer(() =>
    this.store.pipe(
      selectSearchTerm,
      map(searchableTerm),
      distinctUntilChanged(),
      skip(1),
      debounceTime(300),
      startWith(searchableTerm(this.store.getState().searchTerm)),
      distinctUntilChanged(),
    ),
  );

  customCurrentPageData$ = combineLatest([
    this.store.pipe(selectPage, distinctUntilChanged()),
    this.store.pipe(selectSelectedCategory, distinctUntilChanged()),
    this.debouncedSearchTerm$,
    this.store.pipe(selectShowFavouritesOnly, distinctUntilChanged()),
    this.currentDomain$,
  ]).pipe(
    // coalesce the synchronous filter + page-reset emissions into a single request, keeping the
    // initial emission synchronous so the first load is not deferred behind a timer
    switchMap((filters, index) => (index === 0 ? of(filters) : of(filters).pipe(delay(0)))),
    switchMap(([page, category, term, showFavouritesOnly, currentDomain]) =>
      // pagination is 1-based; the API expects a 0-based page index
      this.miReportsService
        .getReports(
          currentDomain,
          page - 1,
          this.pageSize,
          category === '' ? undefined : category,
          term || undefined,
          showFavouritesOnly || undefined,
        )
        .pipe(
          map((data: any) => {
            this.store.setTotal(data.total);
            return data.queries.map((report: any) => ({
              ...report,
              categories: report.categories.map((category: any) => category.name).join(', '),
            }));
          }),
        ),
    ),
  );
}
