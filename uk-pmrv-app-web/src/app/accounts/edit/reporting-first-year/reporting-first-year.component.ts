import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormBuilder, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { InstallationAccountPermitDTO, InstallationAccountUpdateService } from 'pmrv-api';

@Component({
  selector: 'app-account-edit-reporting-first-year',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './reporting-first-year.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountEditReportingFirstYearComponent implements OnInit {
  account = toSignal(
    (
      this.route.data as Observable<{
        accountPermit: InstallationAccountPermitDTO;
      }>
    ).pipe(map((state) => state.accountPermit.account)),
  );

  form = this.fb.group({
    registryReportingFirstYear: [
      null,
      [this.reportingFirstYearValidator(), GovukValidators.naturalNumber('The value must be a positive integer')],
    ],
  });

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly accountUpdateService: InstallationAccountUpdateService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    const account = this.account();

    this.form.patchValue({ registryReportingFirstYear: account.registryReportingFirstYear || null });

    if (account.emitterType === 'GHGE' && account.emissionTradingScheme === 'UK_ETS_INSTALLATIONS') {
      this.form
        .get('registryReportingFirstYear')
        .addValidators([GovukValidators.required('Enter the first year of Registry reporting obligation')]);
    }
  }

  reportingFirstYearValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors => {
      const registryReportingFirstYear = +control.value;
      const minYear = 2021;
      const maxYear = new Date().getFullYear() + 1;

      if (!control.value) {
        return null;
      }

      if (registryReportingFirstYear < minYear) {
        return {
          invalidYear: 'The year must be the same as or after 2021',
        };
      } else if (registryReportingFirstYear > maxYear) {
        return {
          invalidYear: 'The year can only be one year in the future from today',
        };
      } else {
        return null;
      }
    };
  }

  onSubmit() {
    const registryReportingFirstYear = this.form.value.registryReportingFirstYear;
    const account = this.account();

    this.accountUpdateService
      .updateRegistryReportingFirstYear(account.id, { registryReportingFirstYear })
      .pipe(
        this.pendingRequest.trackRequest(),
        catchBadRequest([ErrorCodes.REPFIRSTINVEMISSIONS1000], () => {
          this.form.controls['registryReportingFirstYear'].setErrors({
            invalidRegistryReportingFirstYear:
              'The year must be the same as or before the first year with recorded emissions.',
          });

          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.form.controls['registryReportingFirstYear'].setErrors(null);

        this.router.navigate(['../..'], { relativeTo: this.route });
      });
  }
}
