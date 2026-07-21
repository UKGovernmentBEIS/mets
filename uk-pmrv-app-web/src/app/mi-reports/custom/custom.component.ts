import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, EMPTY, of, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { catchBadRequest, ErrorCodes as BusinessErrorCode } from '@error/business-errors';

import { GovukValidators } from 'govuk-components';

import { CustomMiReportQuery, MiReportsUserDefinedService, MiReportUserDefinedResult } from 'pmrv-api';

import { manipulateResultsAndExportToExcel } from '../core/mi-report';

@Component({
  selector: 'app-custom',
  standalone: false,
  templateUrl: './custom.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomReportComponent {
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  errorMessage$ = new BehaviorSubject<string>(null);

  reportOptionsForm: FormGroup = this.fb.group({
    query: [null, [GovukValidators.required('Query must not be empty')]],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly miReportsUserDefinedService: MiReportsUserDefinedService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  exportToExcel() {
    if (this.reportOptionsForm.valid) {
      this.miReportsUserDefinedService
        .generateCustomReport({
          sqlQuery: this.reportOptionsForm.get('query').value,
        } as CustomMiReportQuery)
        .pipe(
          this.pendingRequest.trackRequest(),
          catchBadRequest(BusinessErrorCode.REPORT1001, (res) => {
            this.errorMessage$.next(res.error.message);
            return EMPTY;
          }),
        )
        .pipe(
          switchMap((results: MiReportUserDefinedResult) => {
            return of(manipulateResultsAndExportToExcel(results, 'Custom sql report'));
          }),
        )
        .subscribe({
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          next: (_) => this.errorMessage$.next(null),
          error: (err) => this.errorMessage$.next(err.message),
        });
    }
  }
}
