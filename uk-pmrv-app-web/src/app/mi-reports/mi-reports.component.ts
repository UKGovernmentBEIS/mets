import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  map,
  Observable,
  shareReplay,
  switchMap,
} from 'rxjs';

import { ConfigService } from '@core/config/config.service';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { MiReportsUserDefinedService, MiReportSystemSearchResult } from 'pmrv-api';

import { miReportTypeDescriptionMap, miReportTypeLinkMap } from './core/mi-report';

@Component({
  selector: 'app-mi-reports',
  standalone: false,
  templateUrl: './mi-reports.component.html',
  styles: `
    .app-task-list__item:first-child {
      border-top-width: 0px;
    }

    button.add-custom-report {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  private readonly configService = inject(ConfigService);

  readonly reportingImprovementsEnabled$ = this.configService.isFeatureEnabled('reportingImprovementsEnabled');
  readonly notification = this.router.currentNavigation()?.extras.state?.notification;
  private readonly standardReportsData$: Observable<MiReportSystemSearchResult[]> = this.route.data.pipe(
    map((data) => data.standardReports),
  );

  readonly pageSize = 10;
  readonly miReportTypeLinkMap = miReportTypeLinkMap;
  readonly miReportTypeDescriptionMap = miReportTypeDescriptionMap;

  customCurrentPage$ = new BehaviorSubject<number>(1);
  customTotalPages$ = new BehaviorSubject(0);
  selectedCategory$ = new BehaviorSubject<number | ''>('');
  customTableColumns: GovukTableColumn[] = [
    { field: 'reportName', header: 'Report name' },
    { field: 'categories', header: 'Category' },
    { field: 'description', header: 'Description' },
  ];

  readonly categoryOptions$: Observable<GovukSelectOption<number>[]> = this.miReportsService
    .getCategories()
    .pipe(map((categories) => categories.map((category) => ({ text: category.name, value: category.id }))));

  onCategoryChange(categoryId: number | ''): void {
    this.selectedCategory$.next(categoryId);
    // reset to the first page so results are not shown against a stale page number
    this.customCurrentPage$.next(1);
  }

  standardCurrentPageData$ = this.standardReportsData$.pipe(
    map((reportsData) =>
      reportsData
        .map((data) => ({ ...data, link: miReportTypeLinkMap[data.miReportType] }))
        .sort((a, b) => a.miReportType.localeCompare(b.miReportType)),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  customCurrentPageData$ = combineLatest([
    this.customCurrentPage$.pipe(distinctUntilChanged()),
    this.selectedCategory$.pipe(distinctUntilChanged()),
  ]).pipe(
    // coalesce the synchronous category + page-reset emissions into a single request
    debounceTime(0),
    switchMap(([page, category]) =>
      // pagination is 1-based; the API expects a 0-based page index
      this.miReportsService.getReports(page - 1, this.pageSize, category === '' ? undefined : category).pipe(
        map((data: any) => {
          this.customTotalPages$.next(data.total);
          return data.queries.map((report: any) => ({
            ...report,
            categories: report.categories.map((category: any) => category.name),
          }));
        }),
      ),
    ),
  );
}
