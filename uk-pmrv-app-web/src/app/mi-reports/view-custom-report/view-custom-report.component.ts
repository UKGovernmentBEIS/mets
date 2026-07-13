import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, filter } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';

import { GovukValidators } from 'govuk-components';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO, MiReportUserDefinedResult } from 'pmrv-api';

import { manipulateResultsAndExportToExcel } from '../core/mi-report';
import { buildGenerateReportError } from '../errors/business-error';

@Component({
  selector: 'app-view-custom-report',
  standalone: false,
  templateUrl: './view-custom-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ViewCustomReportComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  private readonly businessErrorService = inject(BusinessErrorService);
  readonly pendingRequest = inject(PendingRequestService);

  readonly report: MiReportUserDefinedDTO = this.route.snapshot.data.report;
  readonly reportId = Number(this.route.snapshot.paramMap.get('id'));

  readonly categories: string[] = (this.report.categories ?? []).map((category) => category.name);

  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  // The SQL query can be edited before exporting, but those edits are never persisted to the report.
  // Same validation as adding a custom report.
  readonly form = this.fb.group({
    sqlQuery: [this.report.queryDefinition, GovukValidators.required('Query must not be empty')],
  });

  onExport(): void {
    // Validate the query currently shown in the textarea. Read the live value rather than trusting
    // form.valid alone, so a query cleared down to empty/whitespace is always caught.
    const sqlQuery = (this.form.controls.sqlQuery.value ?? '').trim();

    if (!sqlQuery) {
      this.form.controls.sqlQuery.setValue(null);
      this.form.controls.sqlQuery.markAsTouched();
      this.isErrorSummaryDisplayed$.next(true);

      return;
    }

    this.isErrorSummaryDisplayed$.next(false);

    this.miReportsService
      .generateCustomReport({ sqlQuery })
      .pipe(
        catchElseRethrow(
          (res: HttpErrorResponse) =>
            [HttpStatuses.BadRequest, HttpStatuses.Conflict].includes(res.status) &&
            [ErrorCodes.MIREPORT1000, ErrorCodes.MIREPORT1001, ErrorCodes.MIREPORT1002].includes(res.error?.code),
          (res) => this.businessErrorService.showError(buildGenerateReportError(res.error.message, this.reportId)),
        ),
        filter((result): result is MiReportUserDefinedResult => typeof result === 'object' && result !== null),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((result) => manipulateResultsAndExportToExcel(result, this.report.reportName));
  }
}
