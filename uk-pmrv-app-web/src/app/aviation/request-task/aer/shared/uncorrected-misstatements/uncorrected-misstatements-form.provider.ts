import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerUncorrectedMisstatements, UncorrectedItem } from 'pmrv-api';

export interface UncorrectedMisstatementsFormModel {
  exist: FormControl<boolean | null>;
  uncorrectedMisstatements?: FormControl<Array<UncorrectedItem>>;
}

@Injectable()
export class UncorrectedMisstatementsFormProvider
  implements TaskFormProvider<AviationAerUncorrectedMisstatements, UncorrectedMisstatementsFormModel>
{
  private fb = inject(FormBuilder);
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
          GovukValidators.required('Select if there are any misstatements not corrected before issuing the report'),
        ]),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addUncorrectedMisstatementsCtrl();
      } else {
        this.form.removeControl('uncorrectedMisstatements');
      }
    });
  }

  addUncorrectedMisstatementsCtrl() {
    this.form.addControl('uncorrectedMisstatements', this.fb.array([], [GovukValidators.required('')]));
  }

  setFormValue(uncorrectedMisstatements: AviationAerUncorrectedMisstatements | undefined): void {
    const value: any = cloneDeep(uncorrectedMisstatements);
    if (value) {
      this.form.get('exist').patchValue(value.exist);
      if (value?.uncorrectedMisstatements?.length) {
        this.form.setControl('uncorrectedMisstatements', this.fb.array(value?.uncorrectedMisstatements ?? []));
      }
    }
  }

  getFormValue(): AviationAerUncorrectedMisstatements {
    return this.form.value;
  }
}
