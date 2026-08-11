import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, EMPTY, filter, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO, MiReportUserDefinedResult } from 'pmrv-api';

import {
  catchPreviewQueryErrors,
  customReportFormControls,
  CustomReportPreview,
  toCustomReportPreview,
} from '../core/custom-report';
import { manipulateResultsAndExportToExcel } from '../core/mi-report';
import { buildGenerateReportError } from '../errors/business-error';

@Component({
  selector: 'app-view-custom-report',
  standalone: false,
  templateUrl: './view-custom-report.component.html',
  styleUrl: './view-custom-report.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ViewCustomReportComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  private readonly businessErrorService = inject(BusinessErrorService);
  readonly pendingRequest = inject(PendingRequestService);

  readonly report: MiReportUserDefinedDTO = this.route.snapshot.data.report;
  readonly reportId = Number(this.route.snapshot.paramMap.get('id'));

  readonly canManageCustomReports$ = this.miReportsService.hasManageCustomReportsAccess();

  readonly isFavourite$ = new BehaviorSubject<boolean>(this.report.favourite ?? false);

  // set when arriving from a successful save on the edit page
  readonly notification = this.router.currentNavigation()?.extras.state?.notification;

  readonly categories: string[] = (this.report.categories ?? []).map((category) => category.name);

  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  previewResult$ = new BehaviorSubject<CustomReportPreview | null>(null);

  // The SQL query can be edited before exporting, but those edits are never persisted to the report.
  // Same validation as adding a custom report.
  readonly form = this.fb.group(
    { sqlQuery: customReportFormControls(this.report).queryDefinition },
    { updateOn: 'change' },
  );

  onPreview(): void {
    const queryControl = this.form.controls.sqlQuery;
    const sqlQuery = (queryControl.value ?? '').trim();

    if (!sqlQuery) {
      queryControl.setValue(null);
      queryControl.markAsTouched();
      this.isErrorSummaryDisplayed$.next(true);

      return;
    }

    this.isErrorSummaryDisplayed$.next(false);

    this.miReportsService
      .previewCustomReport({ sqlQuery })
      .pipe(catchPreviewQueryErrors(queryControl, this.isErrorSummaryDisplayed$), this.pendingRequest.trackRequest())
      .subscribe((result) => this.previewResult$.next(result ? toCustomReportPreview(result) : null));
  }

  onToggleFavourite(isFavourite: boolean): void {
    if (this.pendingRequest.hasPendingRequests()) {
      return;
    }

    const request$ = isFavourite
      ? this.miReportsService.deleteFavourite(this.reportId)
      : this.miReportsService.createFavourite(this.reportId);

    request$
      .pipe(
        this.pendingRequest.trackRequest(),
        catchElseRethrow(
          (res: HttpErrorResponse) => res instanceof HttpErrorResponse,
          (res) =>
            this.businessErrorService.showError(
              buildGenerateReportError(res.error?.message ?? 'Unable to update favourites', this.reportId),
            ),
        ),
        tap(() => this.isFavourite$.next(!isFavourite)),
      )
      .subscribe();
  }

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
        catchBadRequest(ErrorCodes.REPORT1001, (res) => {
          this.form.controls.sqlQuery.setErrors({
            invalidSqlQuery: res.error?.message ?? 'Enter a valid SQL query',
          });
          this.form.controls.sqlQuery.markAsTouched();
          this.isErrorSummaryDisplayed$.next(true);

          return EMPTY;
        }),
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
