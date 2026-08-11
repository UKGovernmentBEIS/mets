import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, EMPTY, map, Observable, pipe } from 'rxjs';

import { catchBadRequest, catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';

import { GovukSelectOption, GovukTableColumn, GovukValidators } from 'govuk-components';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO, MiReportUserDefinedResult } from 'pmrv-api';

import { createTableColumns } from './mi-report';

/**
 * The form controls shared by the add and edit custom report forms,
 * prefilled when a saved report is provided
 */
export const customReportFormControls = (report?: MiReportUserDefinedDTO) => ({
  reportName: [
    report?.reportName ?? null,
    [
      GovukValidators.required('Enter report name'),
      GovukValidators.maxLength(255, 'The report name should not be more than 255 characters'),
    ],
  ],
  categories: [
    (report?.categories ?? []).map((category) => category.id.toString()),
    GovukValidators.required('Select at least one category'),
  ],
  description: [
    report?.description ?? null,
    [
      GovukValidators.required('Enter description'),
      GovukValidators.maxLength(2000, 'The description should not be more than 2000 characters'),
    ],
  ],
  queryDefinition: [report?.queryDefinition ?? null, GovukValidators.required('Query must not be empty')],
});

/** Maps the multi-select string ids of the categories form value to the API payload */
export const toCategoriesPayload = (categories: Array<string | number>): Array<{ id: number }> =>
  (categories ?? [])
    .map((id) => Number(id))
    .filter((id) => !Number.isNaN(id))
    .map((id) => ({ id }));

export const getCategoryOptions = (
  miReportsService: MiReportsUserDefinedService,
): Observable<GovukSelectOption<number>[]> =>
  miReportsService
    .getCategories()
    .pipe(map((categories) => categories.map((category) => ({ text: category.name, value: category.id }))));

export interface CustomReportSaveErrorContext {
  form: UntypedFormGroup;
  isErrorSummaryDisplayed$: BehaviorSubject<boolean>;
  /** Broadcasts the errors that cannot be shown on a form field (e.g. as a business error) */
  showBusinessError: (message: string) => Observable<unknown>;
}

/**
 * The error handling shared by the create and update custom report requests:
 * invalid SQL queries and existing report names become form errors, anything
 * else known becomes a business error
 */
export const catchCustomReportSaveErrors = ({
  form,
  isErrorSummaryDisplayed$,
  showBusinessError,
}: CustomReportSaveErrorContext) =>
  pipe(
    catchBadRequest(ErrorCodes.FORM1001, () => {
      form.get('queryDefinition').setErrors({
        // The error code FORM1001 is used for invalid SQL queries coming from NETZ library, so we can set a specific error message for that case
        invalidSqlQuery: 'Enter a valid SQL query',
      });
      isErrorSummaryDisplayed$.next(true);

      return EMPTY;
    }),
    catchElseRethrow(
      (res: HttpErrorResponse) =>
        res.status === HttpStatuses.Conflict &&
        [ErrorCodes.MIREPORT1001, ErrorCodes.MIREPORT1002].includes(res.error?.code),
      (res) => {
        if (res.error.code === ErrorCodes.MIREPORT1001) {
          form.get('reportName').setErrors({
            reportNameExists: 'The report name already exists. Enter a different report name.',
          });
          isErrorSummaryDisplayed$.next(true);

          return EMPTY;
        }

        return showBusinessError(res.error.message);
      },
    ),
  );

export interface CustomReportPreview {
  columns: GovukTableColumn[];
  rows: { [key: string]: unknown }[];
}

const previewRowLimit = 10;

/** Maps a generated report result to the first {@link previewRowLimit} rows for the "Preview results" panel */
export const toCustomReportPreview = (result: MiReportUserDefinedResult): CustomReportPreview => ({
  columns: createTableColumns(result.columnNames ?? []),
  rows: (result.results ?? []).slice(0, previewRowLimit),
});

/**
 * The error handling shared by the preview requests across add/edit/view: an invalid SQL query becomes
 * a field error on the query control, mirroring the error handling already used for exporting a report
 */
export const catchPreviewQueryErrors = (
  queryControl: AbstractControl,
  isErrorSummaryDisplayed$: BehaviorSubject<boolean>,
) =>
  pipe(
    catchBadRequest([ErrorCodes.REPORT1001, ErrorCodes.MIREPORT1000], (res: HttpErrorResponse) => {
      queryControl.setErrors({ invalidSqlQuery: res.error?.message ?? 'Enter a valid SQL query' });
      isErrorSummaryDisplayed$.next(true);

      return EMPTY;
    }),
  );
