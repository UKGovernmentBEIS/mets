import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerUncorrectedNonConformities, UncorrectedItem, VerifierComment } from 'pmrv-api';

import { AviationAerUncorrectedNonConformitiesFormModel } from './uncorrected-non-conformities.interface';

@Injectable()
export class UncorrectedNonConformitiesFormProvider
  implements TaskFormProvider<AviationAerUncorrectedNonConformities, AviationAerUncorrectedNonConformitiesFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get existUncorrectedNonConformitiesCtrl() {
    return this.form.get('existUncorrectedNonConformities');
  }

  get uncorrectedNonConformitiesGroup() {
    return this.form.get('uncorrectedNonConformitiesGroup') as FormGroup;
  }

  get uncorrectedNonConformitiesCtrl() {
    return this.uncorrectedNonConformitiesGroup?.get('uncorrectedNonConformities');
  }

  get existPriorYearIssuesCtrl() {
    return this.form.get('existPriorYearIssues');
  }

  get priorYearIssuesGroup() {
    return this.form.get('priorYearIssuesGroup') as FormGroup;
  }

  get priorYearIssuesCtrl() {
    return this.priorYearIssuesGroup?.get('priorYearIssues');
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(uncorrectedNonConformities: AviationAerUncorrectedNonConformities | undefined) {
    if (uncorrectedNonConformities) {
      this.existUncorrectedNonConformitiesCtrl.patchValue(uncorrectedNonConformities.existUncorrectedNonConformities);

      if (uncorrectedNonConformities.existUncorrectedNonConformities) {
        this.uncorrectedNonConformitiesCtrl.setValue(uncorrectedNonConformities.uncorrectedNonConformities);
      }

      this.existPriorYearIssuesCtrl.setValue(uncorrectedNonConformities.existPriorYearIssues);

      if (uncorrectedNonConformities.existPriorYearIssues) {
        this.priorYearIssuesCtrl.setValue(uncorrectedNonConformities.priorYearIssues);
      }
    }
  }

  getFormValue(): AviationAerUncorrectedNonConformities {
    const value: AviationAerUncorrectedNonConformities = {
      existUncorrectedNonConformities: this.existUncorrectedNonConformitiesCtrl?.value ?? null,
      existPriorYearIssues: this.existPriorYearIssuesCtrl?.value ?? null,
    };

    if (this.existUncorrectedNonConformitiesCtrl.value) {
      value.uncorrectedNonConformities = this.uncorrectedNonConformitiesCtrl.value ?? [];
    }

    if (this.existPriorYearIssuesCtrl?.value) {
      value.priorYearIssues = this.priorYearIssuesCtrl.value ?? [];
    }

    return value;
  }

  addUncorrectedNonConformitiesGroup() {
    if (!this.form.contains('uncorrectedNonConformitiesGroup')) {
      this.form.addControl(
        'uncorrectedNonConformitiesGroup',
        this.fb.group({
          uncorrectedNonConformities: this.fb.control([], [Validators.required, Validators.minLength(1)]),
        }),
      );
    }
  }

  removeUncorrectedNonConformitiesGroup() {
    if (this.form.contains('uncorrectedNonConformitiesGroup')) {
      this.form.removeControl('uncorrectedNonConformitiesGroup');
    }
  }

  createUncorrectedNonConformitiesGroup(): FormGroup {
    return this.fb.group(
      {
        explanation: new FormControl<UncorrectedItem['explanation'] | null>(null, [
          GovukValidators.required('Describe the non-conformity'),
          GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
        ]),
        materialEffect: new FormControl<UncorrectedItem['materialEffect'] | null>(null, [
          GovukValidators.required('Select if this has a significant effect on the total emissions reported'),
        ]),
      },
      { updateOn: 'change' },
    );
  }

  addPriorYearIssuesGroup() {
    if (!this.form.contains('priorYearIssuesGroup')) {
      this.form.addControl(
        'priorYearIssuesGroup',
        this.fb.group({
          priorYearIssues: this.fb.control([], [Validators.required, Validators.minLength(1)]),
        }),
      );
    }
  }

  removePriorYearIssuesGroup() {
    if (this.form.contains('priorYearIssuesGroup')) {
      this.form.removeControl('priorYearIssuesGroup');
    }
  }

  createPriorYearIssuesGroup(): FormGroup {
    return this.fb.group(
      {
        explanation: new FormControl<VerifierComment['explanation'] | null>(null, [
          GovukValidators.required('Describe the non-conformity from the previous year that has not been resolved'),
          GovukValidators.maxLength(10000, 'The description should not be more than 10000 characters'),
        ]),
      },
      { updateOn: 'change' },
    );
  }

  private _buildForm() {
    this._form = this.fb.group({
      existUncorrectedNonConformities: new FormControl<
        AviationAerUncorrectedNonConformities['existUncorrectedNonConformities'] | null
      >(null, {
        validators: [
          GovukValidators.required(
            'Select if there have been any uncorrected non-conformities with the approved emissions monitoring plan',
          ),
        ],
        updateOn: 'change',
      }),

      existPriorYearIssues: new FormControl<AviationAerUncorrectedNonConformities['existPriorYearIssues'] | null>(
        null,
        {
          validators: [
            GovukValidators.required(
              'Select if there are any non-conformities from the previous year that have not been resolved',
            ),
          ],
          updateOn: 'change',
        },
      ),
    });

    this.existUncorrectedNonConformitiesCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addUncorrectedNonConformitiesGroup();
      } else {
        this.removeUncorrectedNonConformitiesGroup();
      }

      this.form.updateValueAndValidity();
    });

    this.existPriorYearIssuesCtrl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((exist) => {
      if (exist === true) {
        this.addPriorYearIssuesGroup();
      } else {
        this.removePriorYearIssuesGroup();
      }

      this.form.updateValueAndValidity();
    });
  }
}
