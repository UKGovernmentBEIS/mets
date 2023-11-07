import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { getChangesFormData } from '@aviation/request-task/emp/ukets/tasks/variation-details/util/variation-details-changes.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import {
  EmpUKEtsVariationModification,
  isReasonWizardRequired,
  nonSignificantChanges,
  significantChanges,
} from '@aviation/shared/components/emp/variation-details-summary-template/util/variation-details';

import { GovukValidators } from 'govuk-components';

import { EmpVariationUkEtsDetails, EmpVariationUkEtsRegulatorLedReason } from 'pmrv-api';

export interface VariationDetailsFormModel {
  reason: FormControl<string | null>;
  nonSignificantChanges: FormControl<Array<EmpUKEtsVariationModification['type']> | null>;
  significantChanges: FormControl<Array<EmpUKEtsVariationModification['type']> | null>;
  type?: FormControl<EmpVariationUkEtsRegulatorLedReason['type'] | null>;
  reasonOtherSummary?: FormControl<string | null>;
}

@Injectable()
export class VariationDetailsFormProvider
  implements
    TaskFormProvider<EmpVariationUkEtsDetails & EmpVariationUkEtsRegulatorLedReason, VariationDetailsFormModel>
{
  private _form: FormGroup<VariationDetailsFormModel>;

  constructor(private fb: FormBuilder, private store: RequestTaskStore) {}

  private destroy$ = new Subject<void>();

  get form(): FormGroup<VariationDetailsFormModel> {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get variationDetailsReasonCtrl(): FormControl {
    return this.form.get('reason') as FormControl;
  }

  get variationDetailSignificantChangesCtrl(): FormControl {
    return this.form.get('significantChanges') as FormControl;
  }

  get variationDetailNonSignificantChangesCtrl(): FormControl {
    return this.form.get('nonSignificantChanges') as FormControl;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(variationDetails: (EmpVariationUkEtsDetails & EmpVariationUkEtsRegulatorLedReason) | undefined): void {
    this.form.patchValue({
      significantChanges:
        variationDetails?.changes?.filter((change) => Object.keys(significantChanges).includes(change)) ?? [],
      nonSignificantChanges:
        variationDetails?.changes?.filter((change) => Object.keys(nonSignificantChanges).includes(change)) ?? [],
      reason: variationDetails?.reason ?? null,
      ...(isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)
        ? {
            type: variationDetails?.type ?? null,
            reasonOtherSummary: variationDetails?.reasonOtherSummary ?? null,
          }
        : {}),
    });
  }

  getFormValue(): EmpVariationUkEtsDetails & EmpVariationUkEtsRegulatorLedReason {
    return {
      ...this.getVariationDetailsFormValue(),
      ...this.getVariationRegulatorLedReasonFormValue(),
    };
  }

  getVariationRegulatorLedReasonFormValue(): EmpVariationUkEtsRegulatorLedReason {
    return isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)
      ? {
          type: this.form.get('type').value,
          reasonOtherSummary: this.form.get('reasonOtherSummary').value,
        }
      : null;
  }

  getVariationDetailsFormValue(): EmpVariationUkEtsDetails {
    return {
      reason: this.form.get('reason').value,
      changes: getChangesFormData(this.form.value),
    };
  }

  private _buildForm() {
    this._form = this.fb.group<VariationDetailsFormModel>(
      {
        significantChanges: new FormControl<Array<EmpUKEtsVariationModification['type']> | null>(null, {
          updateOn: 'change',
        }),
        nonSignificantChanges: new FormControl<Array<EmpUKEtsVariationModification['type']> | null>(null, {
          updateOn: 'change',
        }),
        reason: new FormControl<string | null>(null, {
          validators: [
            GovukValidators.required('Enter an explanation of the changes'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters.'),
          ],
        }),
        ...(isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)
          ? {
              type: new FormControl<EmpVariationUkEtsRegulatorLedReason['type'] | null>(null, {
                validators: [GovukValidators.required('Select a reason to include in the notice')],
              }),
              reasonOtherSummary: new FormControl<string | null>(null, {
                validators: [
                  GovukValidators.required('Enter a reason to include in the operator notice'),
                  GovukValidators.maxLength(10000, 'Enter up to 10000 characters.'),
                ],
              }),
            }
          : {}),
      },
      { validators: checkboxSectionsValidator(), updateOn: 'change' },
    );

    if (isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)) {
      this._form
        .get('type')
        .valueChanges.pipe(takeUntil(this.destroy$))
        .subscribe((type) => {
          if (type === 'OTHER') this._form.get('reasonOtherSummary').enable();
          else this._form.get('reasonOtherSummary').disable();
        });
    }
  }
}

export function checkboxSectionsValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors => {
    const isValid = group.get('significantChanges').value?.length || group.get('nonSignificantChanges').value?.length;

    return !isValid ? { invalidForm: 'Select a significant or non-significant change' } : null;
  };
}
