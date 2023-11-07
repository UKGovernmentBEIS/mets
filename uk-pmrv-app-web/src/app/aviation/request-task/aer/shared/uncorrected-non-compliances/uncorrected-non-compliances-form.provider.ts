import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerUncorrectedNonCompliances, UncorrectedItem } from 'pmrv-api';

export interface UncorrectedNonCompliancesFormModel {
  exist: FormControl<boolean | null>;
  uncorrectedNonCompliances?: FormControl<Array<UncorrectedItem>>;
}

@Injectable()
export class UncorrectedNonCompliancesFormProvider
  implements TaskFormProvider<AviationAerUncorrectedNonCompliances, UncorrectedNonCompliancesFormModel>
{
  private fb = inject(FormBuilder);
  private store = inject(RequestTaskStore);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get existCtrl(): FormControl {
    return this.form.get('exist') as FormControl;
  }

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

  private buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl<boolean | null>(null, [
          GovukValidators.required(
            CorsiaRequestTypes.includes(this.store.getState().requestTaskItem?.requestInfo?.type)
              ? 'Select if there have been any non-compliances with the Air Navigation Order'
              : 'Select if there have been any non-compliances with the monitoring and reporting regulations',
          ),
        ]),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addUncorrectedNonCompliancesCtrl();
      } else {
        this.form.removeControl('uncorrectedNonCompliances');
      }
    });
  }

  addUncorrectedNonCompliancesCtrl() {
    this.form.addControl('uncorrectedNonCompliances', this.fb.array([], [GovukValidators.required('')]));
  }

  setFormValue(uncorrectedNonCompliances: AviationAerUncorrectedNonCompliances | undefined): void {
    const value: any = cloneDeep(uncorrectedNonCompliances);
    if (value) {
      this.form.get('exist').patchValue(value.exist);
      if (value?.uncorrectedNonCompliances?.length) {
        this.form.setControl('uncorrectedNonCompliances', this.fb.array(value?.uncorrectedNonCompliances ?? []));
      }
    }
  }

  getFormValue(): AviationAerUncorrectedNonCompliances {
    return this.form.value;
  }
}
