import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

export interface MonitoringPlanChangesFormModel {
  notCoveredChangesExist: FormControl<boolean | null>;
  details?: FormControl<string | null>;
}

@Injectable()
export class MonitoringPlanChangesFormProvider
  implements TaskFormProvider<AviationAerMonitoringPlanChanges, MonitoringPlanChangesFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(monitoringPlanChanges: AviationAerMonitoringPlanChanges | undefined): void {
    if (monitoringPlanChanges) {
      this.form.patchValue(monitoringPlanChanges);
    }
  }

  getFormValue(): AviationAerMonitoringPlanChanges {
    return this.form.value as AviationAerMonitoringPlanChanges;
  }

  private buildForm() {
    const formGroup = this.fb.group(
      {
        notCoveredChangesExist: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
        }),
        details: new FormControl<string | null>(null),
      },
      { updateOn: 'change' },
    );

    const notCoveredChangesExist = formGroup.get('notCoveredChangesExist');
    const details = formGroup.get('details');

    notCoveredChangesExist.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
      if (value) {
        details.setValidators([
          GovukValidators.required('Enter any changes that were not covered in the reporting year'),
          GovukValidators.maxLength(
            10000,
            'The changes not covered in the reporting year must be 10,000 characters or fewer',
          ),
        ]);
      } else {
        details.clearValidators();
        details.setValue(null);
      }
      details.updateValueAndValidity();
    });

    return (this._form = formGroup);
  }
}
