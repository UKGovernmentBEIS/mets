import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerDataGapsMethodologies } from 'pmrv-api';

export interface DataGapsMethodologiesFormModel {
  methodRequired: FormControl<boolean | null>;
}

@Injectable()
export class DataGapsMethodologiesFormProvider
  implements TaskFormProvider<AviationAerDataGapsMethodologies, DataGapsMethodologiesFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get methodRequiredCtrl(): FormControl {
    return this.form.get('methodRequired') as FormControl;
  }

  get methodApprovedCtrl(): FormControl {
    return this.form.get('methodApproved') as FormControl;
  }

  get methodConservativeCtrl(): FormControl {
    return this.form.get('methodConservative') as FormControl;
  }

  get noConservativeMethodDetailsCtrl(): FormControl {
    return this.form.get('noConservativeMethodDetails') as FormControl;
  }

  get materialMisstatementExistCtrl(): FormControl {
    return this.form.get('materialMisstatementExist') as FormControl;
  }

  get materialMisstatementDetailsCtrl(): FormControl {
    return this.form.get('materialMisstatementDetails') as FormControl;
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
        methodRequired: new FormControl<boolean | null>(null, [
          GovukValidators.required('Select if a data gap method was required during the reporting year'),
        ]),
      },
      { updateOn: 'change' },
    );

    this.methodRequiredCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((methodRequired) => {
      if (methodRequired === true) {
        this.addMethodApprovedCtrl();
      } else {
        this.form.removeControl('methodApproved');
        this.form.removeControl('methodConservative');
        this.form.removeControl('noConservativeMethodDetails');
        this.form.removeControl('materialMisstatementExist');
        this.form.removeControl('materialMisstatementDetails');
      }
    });
  }

  addMethodApprovedCtrl() {
    const formControl = this.fb.control(null, [
      GovukValidators.required('Select if the data gap method has already been approved by the regulator'),
    ]);
    formControl?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((methodApproved) => {
      if (methodApproved === false) {
        this.addMethodConservativeCtrl();
      } else {
        this.form.removeControl('methodConservative');
        this.form.removeControl('noConservativeMethodDetails');
        this.form.removeControl('materialMisstatementExist');
        this.form.removeControl('materialMisstatementDetails');
      }
    });
    (this.form as FormGroup).addControl('methodApproved', formControl);
  }

  addMethodConservativeCtrl() {
    const formControl = this.fb.control(null, [
      GovukValidators.required('Select if the method used to complete the data gap was conservative'),
    ]);
    formControl?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((methodConservative) => {
      if (methodConservative === false) {
        this.noConservativeMethodDetailsCtrl.setValidators([
          GovukValidators.required('Provide more detail as to why the method used was not conservative'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      } else {
        this.noConservativeMethodDetailsCtrl.clearValidators();
        this.noConservativeMethodDetailsCtrl.reset();
      }
      this.noConservativeMethodDetailsCtrl.updateValueAndValidity();
    });
    (this.form as FormGroup).addControl('methodConservative', formControl);
    (this.form as FormGroup).addControl('noConservativeMethodDetails', this.fb.control(null));
    this.addMaterialMisstatementCtrl();
  }

  addMaterialMisstatementCtrl() {
    const formControl = this.fb.control(null, [
      GovukValidators.required('Select if the method used led to a significant misstatement'),
    ]);

    formControl?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((materialMisstatementExist) => {
      if (materialMisstatementExist === true) {
        this.materialMisstatementDetailsCtrl.setValidators([
          GovukValidators.required('Provide more detail why the method led to a material misstatement'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      } else {
        this.materialMisstatementDetailsCtrl.clearValidators();
        this.materialMisstatementDetailsCtrl.reset();
      }
      this.materialMisstatementDetailsCtrl.updateValueAndValidity();
    });

    (this.form as FormGroup).addControl('materialMisstatementExist', formControl);
    (this.form as FormGroup).addControl('materialMisstatementDetails', this.fb.control(null));
  }

  setFormValue(dataGapsMethodologies: AviationAerDataGapsMethodologies | undefined): void {
    if (dataGapsMethodologies) {
      this.form.patchValue(dataGapsMethodologies);
    }
  }

  getFormValue(): AviationAerDataGapsMethodologies {
    return this.form.value;
  }
}
