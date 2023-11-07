import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AccountTypePipe } from '@shared/pipes/account-type.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { WorkflowTypePipe } from '@shared/pipes/workflow-type.pipe';

import { GovukTableColumn } from 'govuk-components';

import {
  CustomMiReportResult,
  ItemDTO,
  MiReportsService,
  OutstandingRegulatorRequestTasksMiReportParams,
  OutstandingRequestTask,
  OutstandingRequestTasksMiReportResult,
  RegulatorAuthoritiesService,
} from 'pmrv-api';

import { createTableColumns, createTablePage, manipulateResultsAndExportToExcel, pageSize } from '../core/mi-report';

@Component({
  selector: 'app-regulator-outstanding-request-tasks',
  templateUrl: './regulator-outstanding-request-tasks.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class RegulatorOutstandingRequestTasksComponent implements OnInit {
  taskTypes$: Observable<ItemDTO['taskType'][]>;

  regulators$: Observable<{ value: string; label: string }[]>;

  readonly pageSize = pageSize;
  reportItems$: Observable<OutstandingRequestTask[]>;
  pageItems$: Observable<OutstandingRequestTask[]>;
  totalNumOfItems$: Observable<number>;

  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();

  reportOptionsForm: FormGroup = this.fb.group({
    taskTypes: [],
    regulators: [],
  });

  tableColumns: GovukTableColumn<OutstandingRequestTask>[];
  domain: string;
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  constructor(
    private readonly fb: FormBuilder,
    private readonly miReportsService: MiReportsService,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
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

    this.taskTypes$ = this.currentDomain$
      .pipe(switchMap((currentDomain) => this.miReportsService.getRegulatorRequestTaskTypes(currentDomain)))
      .pipe(map((taskTypes) => Array.from(taskTypes.values()) as Array<ItemDTO['taskType']>));

    this.regulators$ = this.regulatorAuthoritiesService.getCaRegulators().pipe(
      map((res) =>
        Array.from(
          res.caUsers.map((regulator) => {
            return { value: regulator.userId, label: `${regulator.firstName} ${regulator.lastName}` };
          }),
        ),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.reportItems$ = combineLatest([this.currentDomain$, this.generateReport$]).pipe(
      switchMap(([currentDomain]) => this.miReportsService.generateReport(currentDomain, this.constructRequestBody())),
      tap(
        (miReportResult: OutstandingRequestTasksMiReportResult) =>
          (this.tableColumns = createTableColumns(miReportResult.columnNames)),
      ),
      map((miReportResult) => this.addPipesToResult(miReportResult.results)),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.pageItems$ = combineLatest([this.reportItems$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );
    this.totalNumOfItems$ = this.reportItems$.pipe(map((items) => items?.length));
  }

  onSubmit() {
    this.generateReport$.next();
    this.router.navigate([], { relativeTo: this.route, queryParams: { page: 1 }, queryParamsHandling: 'merge' });
  }

  addPipesToResult(results: OutstandingRequestTasksMiReportResult['results']) {
    return results.map((outstandingRequestTask) => {
      const accountTypePipe = new AccountTypePipe();
      const workflowTypePipe = new WorkflowTypePipe();
      const itemNamePipe = new ItemNamePipe();
      const govukDatePipe = new GovukDatePipe();

      return {
        ...outstandingRequestTask,
        'Account type': accountTypePipe.transform(outstandingRequestTask['Account type']),
        'Workflow type': workflowTypePipe.transform(outstandingRequestTask['Workflow type']),
        'Workflow task name': itemNamePipe.transform(outstandingRequestTask['Workflow task name']),
        'Workflow task due date': govukDatePipe.transform(outstandingRequestTask['Workflow task due date']),
      };
    });
  }

  exportToExcel() {
    if (this.reportOptionsForm.valid) {
      this.currentDomain$
        .pipe(
          switchMap((currentDomain) =>
            this.miReportsService.generateReport(currentDomain, this.constructRequestBody()),
          ),
        )
        .pipe(
          map((miReportResult: CustomMiReportResult) =>
            manipulateResultsAndExportToExcel(
              { ...miReportResult, results: this.addPipesToResult(miReportResult.results) },
              'Regulator outstanding request tasks',
            ),
          ),
        )
        .subscribe();
    }
  }

  private constructRequestBody(): OutstandingRegulatorRequestTasksMiReportParams {
    return {
      reportType: 'REGULATOR_OUTSTANDING_REQUEST_TASKS',
      requestTaskTypes: this.reportOptionsForm.get('taskTypes').value ?? [],
      userIds: this.reportOptionsForm.get('regulators').value ?? [],
    };
  }
}
