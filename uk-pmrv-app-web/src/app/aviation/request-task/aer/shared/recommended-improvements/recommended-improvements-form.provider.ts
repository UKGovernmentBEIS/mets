import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { cloneDeep } from 'lodash-es';

import { GovukValidators } from 'govuk-components';

import { AviationAerRecommendedImprovements, VerifierComment } from 'pmrv-api';

export interface RecommendedImprovementsFormModel {
  exist: FormControl<boolean | null>;
  recommendedImprovements?: FormControl<Array<VerifierComment>>;
}

@Injectable()
export class RecommendedImprovementsFormProvider
  implements TaskFormProvider<AviationAerRecommendedImprovements, RecommendedImprovementsFormModel>
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
          GovukValidators.required('Select if there are any recommended improvements'),
        ]),
      },
      { updateOn: 'change' },
    );

    this.existCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addRecommendedImprovementsCtrl();
      } else {
        this.form.removeControl('recommendedImprovements');
      }
    });
  }

  addRecommendedImprovementsCtrl() {
    this.form.addControl('recommendedImprovements', this.fb.array([], [GovukValidators.required('')]));
  }

  setFormValue(recommendedImprovements: AviationAerRecommendedImprovements | undefined): void {
    const value: any = cloneDeep(recommendedImprovements);
    if (value) {
      this.form.get('exist').patchValue(value.exist);
      if (value?.recommendedImprovements?.length) {
        this.form.setControl('recommendedImprovements', this.fb.array(value?.recommendedImprovements ?? []));
      }
    }
  }

  getFormValue(): AviationAerRecommendedImprovements {
    return this.form.value;
  }
}
