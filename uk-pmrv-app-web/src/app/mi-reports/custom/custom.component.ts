import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, EMPTY, of, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';
import { catchBadRequest, ErrorCodes as BusinessErrorCode } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { CustomMiReportParams, MiReportsService } from 'pmrv-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { manipulateResultsAndExportToExcel } from '../core/mi-report';

@Component({
  selector: 'app-custom',
  templateUrl: './custom.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CustomReportComponent implements OnInit {
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  errorMessage$ = new BehaviorSubject<string>(null);
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));
  domain: string;

  reportOptionsForm: FormGroup = this.fb.group({
    query: [null, [GovukValidators.required('Query must not be empty')]],
  });

  constructor(
    private readonly backlinkService: BackLinkService,
    private readonly fb: FormBuilder,
    private readonly miReportsService: MiReportsService,
    readonly pendingRequest: PendingRequestService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.currentDomain$.pipe(takeUntil(this.destroy$)).subscribe((domain) => {
      this.domain = domain === 'AVIATION' ? domain.toLowerCase() : '';
    });
    this.backlinkService.show(this.domain + '/mi-reports');
  }

  exportToExcel() {
    if (this.reportOptionsForm.valid) {
      this.currentDomain$
        .pipe(
          switchMap((currentDomain) =>
            this.miReportsService.generateCustomReport(currentDomain, {
              reportType: 'CUSTOM',
              sqlQuery: this.reportOptionsForm.get('query').value,
            } as CustomMiReportParams),
          ),
        )
        .pipe(
          this.pendingRequest.trackRequest(),
          catchBadRequest(BusinessErrorCode.REPORT1001, (res) => {
            this.errorMessage$.next(res.error.message);
            return EMPTY;
          }),
        )
        .pipe(
          switchMap((results: ExtendedMiReportResult) => {
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
