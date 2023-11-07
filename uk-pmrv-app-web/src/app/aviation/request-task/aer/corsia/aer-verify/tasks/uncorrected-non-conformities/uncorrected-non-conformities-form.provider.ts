import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaUncorrectedNonConformities, UncorrectedItem } from 'pmrv-api';

export interface AviationAerCorsiaUncorrectedNonConformitiesFormModel {
  existUncorrectedNonConformities: FormControl<boolean | null>;
  uncorrectedNonConformities?: FormControl<Array<UncorrectedItem>>;
}

@Injectable()
export class UncorrectedNonConformitiesFormProvider
  implements
    TaskFormProvider<AviationAerCorsiaUncorrectedNonConformities, AviationAerCorsiaUncorrectedNonConformitiesFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get existCtrl(): FormControl {
    return this.form.get('existUncorrectedNonConformities') as FormControl;
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
        existUncorrectedNonConformities: new FormControl<boolean | null>(null, [
          GovukValidators.required(
            'Select if there have been any uncorrected non-conformities with the approved emissions monitoring plan',
          ),
        ]),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addUncorrectedNonConformitiesCtrl();
      } else {
        this.form.removeControl('uncorrectedNonConformities');
      }
    });
  }

  addUncorrectedNonConformitiesCtrl() {
    this.form.addControl('uncorrectedNonConformities', this.fb.array([], [GovukValidators.required('')]));
  }

  setFormValue(uncorrectedNonConformities: AviationAerCorsiaUncorrectedNonConformities | undefined): void {
    const value: any = cloneDeep(uncorrectedNonConformities);
    if (value) {
      this.form.get('existUncorrectedNonConformities').patchValue(value.existUncorrectedNonConformities);
      if (value?.uncorrectedNonConformities?.length) {
        this.form.setControl('uncorrectedNonConformities', this.fb.array(value?.uncorrectedNonConformities ?? []));
      }
    }
  }

  getFormValue(): AviationAerCorsiaUncorrectedNonConformities {
    return this.form.value;
  }
}
