import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukTableColumn } from 'govuk-components';

import { MiReportsService, VerificationBodyUser, VerificationBodyUsersMiReportResult } from 'pmrv-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { createTableColumns, createTablePage, manipulateResultsAndExportToExcel, pageSize } from '../core/mi-report';
import { AuthorityStatusPipe } from '../pipes/authority-status.pipe';
import { VerificationBodyStatusPipe } from '../pipes/verification-body-status.pipe';

@Component({
  selector: 'app-verification-bodies-users',
  template: `
    <app-page-heading size="xl">List of Verification bodies and Users</app-page-heading>
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
export class VerificationBodiesUsersComponent implements OnInit {
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  verificationBodiesUsers$ = this.currentDomain$
    .pipe(
      switchMap((currentDomain) =>
        this.miReportsService.generateReport(currentDomain, { reportType: 'LIST_OF_VERIFICATION_BODY_USERS' }),
      ),
    )
    .pipe(
      map((miReportResult: VerificationBodyUsersMiReportResult) => ({
        ...miReportResult,
        results: this.addPipesToResult(miReportResult.results),
      })),
    );
  reportItems$: Observable<VerificationBodyUser[]>;
  pageItems$: Observable<VerificationBodyUser[]>;
  totalNumOfItems$: Observable<number>;

  initialPageNumber = 1;
  readonly pageSize = pageSize;
  readonly currentPage$ = new BehaviorSubject<number>(this.initialPageNumber);
  readonly generateReport$ = new Subject<void>();

  tableColumns: GovukTableColumn<VerificationBodyUser>[];
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
      switchMap(() => this.verificationBodiesUsers$),
      tap(
        (miReportResult: VerificationBodyUsersMiReportResult) =>
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
    this.router
      .navigate([], {
        relativeTo: this.route,
        queryParams: { page: this.initialPageNumber },
        queryParamsHandling: 'merge',
      })
      .then();
  }

  addPipesToResult(results: VerificationBodyUsersMiReportResult['results']) {
    return results.map((accountAssignedRegulatorSiteContact) => {
      const verificationBodyStatusPipe = new VerificationBodyStatusPipe();
      const authorityStatusPipe = new AuthorityStatusPipe();

      return {
        ...accountAssignedRegulatorSiteContact,
        'Account status': verificationBodyStatusPipe.transform(accountAssignedRegulatorSiteContact['Account status']),
        'User status': authorityStatusPipe.transform(accountAssignedRegulatorSiteContact['User status']),
      };
    });
  }

  exportToExcel() {
    this.verificationBodiesUsers$
      .pipe(
        map((miReportResult: ExtendedMiReportResult) =>
          manipulateResultsAndExportToExcel(miReportResult, 'Verification bodies and Users'),
        ),
      )
      .subscribe();
  }
}
