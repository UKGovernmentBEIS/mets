import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap, take, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { AccountTypePipe } from '@shared/pipes/account-type.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { WorkflowStatusPipe } from '@shared/pipes/workflow-status.pipe';
import { WorkflowTypePipe } from '@shared/pipes/workflow-type.pipe';
import { format, subDays } from 'date-fns';

import { GovukTableColumn, GovukValidators } from 'govuk-components';

import {
  CustomMiReportResult,
  ExecutedRequestAction,
  ExecutedRequestActionsMiReportParams,
  ExecutedRequestActionsMiReportResult,
  MiReportsService,
} from 'pmrv-api';

import { DateInputValidators } from '../../../../projects/govuk-components/src/lib/date-input/date-input.validators';
import { createTableColumns, createTablePage, manipulateResultsAndExportToExcel, pageSize } from '../core/mi-report';

@Component({
  selector: 'app-completed-work',
  templateUrl: './completed-work.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CompletedWorkComponent implements OnInit {
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, take(1));
  readonly pageSize = pageSize;

  reportItems$: Observable<ExecutedRequestAction[]>;
  pageItems$: Observable<ExecutedRequestAction[]>;
  totalNumOfItems$: Observable<number>;
  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();

  executeClicked = false;
  domain: string;

  reportOptionsForm: FormGroup = this.fb.group({
    option: [null, [GovukValidators.required('Select an option')]],
    year: [
      null,
      [
        GovukValidators.required('Enter a year value'),
        GovukValidators.builder(
          `Enter a valid year value e.g. 2022`,
          DateInputValidators.dateFieldValidator('year', 1900, 2100),
        ),
      ],
    ],
  });

  tableColumns: GovukTableColumn<ExecutedRequestAction>[];

  constructor(
    private readonly fb: FormBuilder,
    private readonly miReportsService: MiReportsService,
    private readonly backlinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? domain.toLowerCase() : '';
    });

    this.backlinkService.show(this.domain + '/mi-reports');

    this.reportItems$ = combineLatest([this.currentDomain$, this.generateReport$]).pipe(
      switchMap(([currentDomain]) => this.miReportsService.generateReport(currentDomain, this.constructRequestBody())),
      tap(
        (miReportResult: ExecutedRequestActionsMiReportResult) =>
          (this.tableColumns = createTableColumns(miReportResult.columnNames)),
      ),
      map((miReportResult) => this.addPipesToResult(miReportResult.results)),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.pageItems$ = combineLatest([this.reportItems$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );

    this.totalNumOfItems$ = this.reportItems$.pipe(map((items) => items.length));
  }

  onSubmit() {
    if (this.reportOptionsForm.valid) {
      if (this.executeClicked) {
        this.generateReport$.next();
        this.router.navigate([], { relativeTo: this.route, queryParams: { page: 1 }, queryParamsHandling: 'merge' });
      } else {
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
                'Completed work',
              ),
            ),
          )
          .subscribe();
      }
    }
  }

  addPipesToResult(results: ExecutedRequestActionsMiReportResult['results']) {
    return results.map((completedWork) => {
      const accountTypePipe = new AccountTypePipe();
      const accountStatusPipe = new AccountStatusPipe();
      const workflowTypePipe = new WorkflowTypePipe();
      const workflowStatusPipe = new WorkflowStatusPipe();
      const itemActionTypePipe = new ItemActionTypePipe();
      const govukDatePipe = new GovukDatePipe();

      return {
        ...completedWork,
        'Account type': accountTypePipe.transform(completedWork['Account type']),
        'Account status': accountStatusPipe.transform(completedWork['Account status']),
        'Workflow type': workflowTypePipe.transform(completedWork['Workflow type']),
        'Workflow status': workflowStatusPipe.transform(completedWork['Workflow status']),
        'Timeline event type': itemActionTypePipe.transform(completedWork['Timeline event type']),
        'Timeline event Date Completed': govukDatePipe.transform(completedWork['Timeline event Date Completed']),
      };
    });
  }

  private constructRequestBody(): ExecutedRequestActionsMiReportParams {
    switch (this.reportOptionsForm.get('option').value) {
      case 'LAST_30_DAYS': {
        return {
          reportType: 'COMPLETED_WORK',
          fromDate: format(subDays(new Date(), 30), 'yyyy-MM-dd'),
        };
      }

      case 'ANNUAL': {
        return {
          reportType: 'COMPLETED_WORK',
          fromDate: format(new Date(this.reportOptionsForm.get('year').value, 0, 1), 'yyyy-MM-dd'),
          toDate: format(new Date(Number(this.reportOptionsForm.get('year').value) + 1, 0, 1), 'yyyy-MM-dd'),
        };
      }
    }
  }

  onExecuteClicked(): void {
    this.executeClicked = true;
  }

  onExportClicked(): void {
    this.executeClicked = false;
  }
}
