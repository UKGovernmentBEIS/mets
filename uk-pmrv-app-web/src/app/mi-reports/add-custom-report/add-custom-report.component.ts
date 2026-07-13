import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';

import { GovukSelectOption } from 'govuk-components';

import { MiReportsUserDefinedService } from 'pmrv-api';

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

  categoryOptions$: Observable<GovukSelectOption<number>[]> = this.miReportsService
    .getCategories()
    .pipe(map((categories) => categories.map((category) => ({ text: category.name, value: category.id }))));

  constructor(
    @Inject(ADD_CUSTOM_REPORT_FORM) readonly form: UntypedFormGroup,
    private readonly miReportsService: MiReportsUserDefinedService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.valid) {
      this.isErrorSummaryDisplayed$.next(true);

      return;
    }

    const { reportName, categories, description, queryDefinition } = this.form.value;

    this.miReportsService
      .createCustomReport({
        reportName,
        description,
        queryDefinition,
        categories: ((categories as Array<string | number>) ?? [])
          .map((id) => Number(id))
          .filter((id) => !Number.isNaN(id))
          .map((id) => ({ id })),
      })
      .pipe(
        catchElseRethrow(
          (res: HttpErrorResponse) =>
            res.status === HttpStatuses.Conflict &&
            [ErrorCodes.MIREPORT1001, ErrorCodes.MIREPORT1002].includes(res.error?.code),
          (res) => this.businessErrorService.showError(buildCustomReportError(res.error.message)),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route, state: { notification: true } }));
  }
}
