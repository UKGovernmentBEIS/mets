import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { Router } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { AviationAccountReportingStatusHistoryCreationDTO } from 'pmrv-api';

import { AviationAccountsStore } from '../../store';

interface FormModel {
  status: FormControl<AviationAccountReportingStatusHistoryCreationDTO['status']>;
  reason: FormControl<string>;
}

@Component({
  selector: 'app-edit-reporting-status',
  templateUrl: './edit-reporting-status.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EditReportingStatusComponent {
  private currentStatus = this.store.getState().currentAccount.account.aviationAccount.reportingStatus;
  private currentReason = this.store.getState().currentAccount.account.aviationAccount.reportingStatusReason;

  statusOptions: GovukSelectOption[] = [
    {
      text: 'Required to report',
      value: 'REQUIRED_TO_REPORT',
    },
    {
      text: 'Exempt (commercial)',
      value: 'EXEMPT_COMMERCIAL',
    },
    {
      text: 'Exempt (non commercial)',
      value: 'EXEMPT_NON_COMMERCIAL',
    },
  ];
  maxReasonLength = 2000;
  form: FormGroup<FormModel> = this.fb.group<FormModel>({
    status: new FormControl(this.currentStatus ?? null, {
      validators: [this.validateStatusChanged(this.currentStatus)],
    }),
    reason: new FormControl(this.currentReason ?? null, {
      validators: [
        GovukValidators.required('Enter a reason'),
        GovukValidators.maxLength(
          this.maxReasonLength,
          `The reason should not be more than ${this.maxReasonLength} characters`,
        ),
      ],
    }),
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
  ) {}

  onSubmit() {
    if (this.form.valid) {
      this.store
        .editReportingStatus(this.form.value as AviationAccountReportingStatusHistoryCreationDTO)
        .subscribe(() => {
          this.router.navigate(['aviation/accounts', this.store.getState().currentAccount.account.aviationAccount.id]);
        });
    }
  }

  private validateStatusChanged(
    currentStatus: AviationAccountReportingStatusHistoryCreationDTO['status'],
  ): ValidatorFn {
    return (control) => {
      const newStatus = control.value;

      return newStatus == null || newStatus !== currentStatus ? null : { status: 'Enter a different reporting status' };
    };
  }
}
