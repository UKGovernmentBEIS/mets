import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { AviationAccountReportingStatusHistoryCreationDTO } from 'pmrv-api';

import { AviationAccountsStore } from '../../store';

interface FormModel {
  status: FormControl<AviationAccountReportingStatusHistoryCreationDTO['status']>;
  reason: FormControl<string>;
}

@Component({
  selector: 'app-edit-reporting-status',
  standalone: false,
  templateUrl: './edit-reporting-status.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditReportingStatusComponent {
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

  private readonly store = inject(AviationAccountsStore);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  public readonly currentStatus = this.store.getState().currentAccount?.reportingStatus?.currentStatus;
  public readonly upsertStatus = this.store.getState().currentAccount?.reportingStatus?.upsertStatus;

  maxReasonLength = 2000;
  form: FormGroup<FormModel> = this.fb.group<FormModel>({
    status: new FormControl(this.currentStatus?.status ?? null, {
      validators: [this.validateStatusChanged(this.currentStatus?.status)],
    }),
    reason: new FormControl(this.upsertStatus?.reason, {
      validators: [
        GovukValidators.required('Enter a reason'),
        GovukValidators.maxLength(this.maxReasonLength, `Enter up to ${this.maxReasonLength} characters`),
      ],
    }),
  });

  onSubmit() {
    this.store.editReportingStatus({
      ...this.form.value,
      year: +this.currentStatus.year,
    } as AviationAccountReportingStatusHistoryCreationDTO);
    this.router.navigate(['./summary'], { relativeTo: this.activatedRoute });
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
