import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, distinctUntilChanged, map, shareReplay, switchMap } from 'rxjs';

import { MiReportsUserDefinedService } from 'pmrv-api';

@Component({
  selector: 'app-report-history',
  standalone: false,
  templateUrl: './report-history.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportHistoryComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly miReportsService = inject(MiReportsUserDefinedService);

  readonly pageSize = 3;
  readonly reportId = Number(this.route.snapshot.paramMap.get('id'));

  readonly currentPage$ = new BehaviorSubject<number>(
    Math.max(1, Number(this.route.snapshot.queryParamMap.get('page') ?? '1') || 1),
  );

  readonly history$ = this.currentPage$.pipe(
    map((page) => Math.max(1, Math.floor(page) || 1)),
    distinctUntilChanged(),
    switchMap((page) => {
      const safePage = Number.isFinite(page) ? Math.max(1, page) : 1;
      return this.miReportsService.getHistory(this.reportId, safePage - 1, this.pageSize);
    }),
    map((history) => ({
      total: history.total ?? 0,
      events: (history.results ?? []).map((event) => ({
        ...event,
        // the snapshot categories come as a single comma-separated string
        categories: (event.categories ?? '')
          .split(',')
          .map((category) => category.trim())
          .filter(Boolean),
      })),
    })),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
}
