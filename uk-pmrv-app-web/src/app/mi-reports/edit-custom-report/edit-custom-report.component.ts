import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'pmrv-api';

import {
  catchCustomReportSaveErrors,
  catchPreviewQueryErrors,
  CustomReportPreview,
  getCategoryOptions,
  toCategoriesPayload,
  toCustomReportPreview,
} from '../core/custom-report';
import { buildEditCustomReportError } from '../errors/business-error';
import { EDIT_CUSTOM_REPORT_FORM, editCustomReportFormProvider } from './edit-custom-report-form.provider';

@Component({
  selector: 'app-edit-custom-report',
  standalone: false,
  templateUrl: './edit-custom-report.component.html',
  providers: [editCustomReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditCustomReportComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly miReportsService = inject(MiReportsUserDefinedService);
  private readonly businessErrorService = inject(BusinessErrorService);
  readonly pendingRequest = inject(PendingRequestService);
  readonly form = inject<UntypedFormGroup>(EDIT_CUSTOM_REPORT_FORM);

  readonly report: MiReportUserDefinedDTO = this.route.snapshot.data.report;
  readonly reportId = Number(this.route.snapshot.paramMap.get('id'));

  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  previewResult$ = new BehaviorSubject<CustomReportPreview | null>(null);

  categoryOptions$ = getCategoryOptions(this.miReportsService);

  onPreview(): void {
    const queryControl = this.form.get('queryDefinition');
    const sqlQuery = (queryControl.value ?? '').trim();

    if (!sqlQuery) {
      queryControl.setValue(null);
      queryControl.markAsTouched();
      this.isErrorSummaryDisplayed$.next(true);

      return;
    }

    this.miReportsService
      .previewCustomReport({ sqlQuery })
      .pipe(catchPreviewQueryErrors(queryControl, this.isErrorSummaryDisplayed$), this.pendingRequest.trackRequest())
      .subscribe((result) => this.previewResult$.next(result ? toCustomReportPreview(result) : null));
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.isErrorSummaryDisplayed$.next(true);

      return;
    }

    const { reportName, categories, description, queryDefinition, reasonForChange } = this.form.value;

    this.miReportsService
      .updateCustomReport(this.reportId, {
        userDefinedDTO: {
          reportName,
          description,
          queryDefinition,
          categories: toCategoriesPayload(categories),
        },
        reasonForChange,
      })
      .pipe(
        catchCustomReportSaveErrors({
          form: this.form,
          isErrorSummaryDisplayed$: this.isErrorSummaryDisplayed$,
          showBusinessError: (message) =>
            this.businessErrorService.showError(buildEditCustomReportError(message, this.reportId)),
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate(['../../view-custom-report', this.reportId], {
          relativeTo: this.route,
          state: { notification: 'The report has been saved' },
        }),
      );
  }
}
