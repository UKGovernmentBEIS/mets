import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, switchMap, take } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';

import { MiReportsUserDefinedService } from 'pmrv-api';

import {
  catchCustomReportSaveErrors,
  catchPreviewQueryErrors,
  CustomReportPreview,
  getCategoryOptions,
  toCategoriesPayload,
  toCustomReportPreview,
} from '../core/custom-report';
import { buildCustomReportError } from '../errors/business-error';
import { ADD_CUSTOM_REPORT_FORM, addCustomReportFormProvider } from './add-custom-report-form.provider';

@Component({
  selector: 'app-add-custom-report',
  standalone: false,
  templateUrl: './add-custom-report.component.html',
  providers: [addCustomReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddCustomReportComponent {
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  previewResult$ = new BehaviorSubject<CustomReportPreview | null>(null);

  categoryOptions$ = getCategoryOptions(this.miReportsService);

  constructor(
    @Inject(ADD_CUSTOM_REPORT_FORM) readonly form: UntypedFormGroup,
    private readonly authStore: AuthStore,
    private readonly miReportsService: MiReportsUserDefinedService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
  ) {}

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

    const { reportName, categories, description, queryDefinition } = this.form.value;

    this.authStore
      .pipe(
        selectCurrentDomain,
        switchMap((domain) => (domain ? [domain] : [])),
        take(1),
        switchMap((currentDomain) =>
          this.miReportsService.createCustomReport(currentDomain, {
            reportName,
            description,
            queryDefinition,
            categories: toCategoriesPayload(categories),
          }),
        ),
        catchCustomReportSaveErrors({
          form: this.form,
          isErrorSummaryDisplayed$: this.isErrorSummaryDisplayed$,
          showBusinessError: (message) => this.businessErrorService.showError(buildCustomReportError(message)),
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate(['../'], { relativeTo: this.route, state: { notification: 'Report saved' } }),
      );
  }
}
