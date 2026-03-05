import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { Subject } from 'rxjs';

import { getChangesFormData } from '@aviation/request-task/emp/corsia/tasks/variation-details/util/variation-details-changes.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import {
  EmpCorsiaVariationModification,
  isReasonWizardRequired,
  materialChanges,
  nonMaterialChanges,
  otherChanges,
} from '@aviation/shared/components/emp-corsia/variation-details-summary-template/util/variation-details';

import { GovukValidators } from 'govuk-components';

import { EmpVariationCorsiaDetails } from 'pmrv-api';

export interface VariationDetailsFormModel {
  reason: FormControl<string | null>;
  materialChanges: FormControl<Array<EmpCorsiaVariationModification['type']> | null>;
  otherChanges: FormControl<Array<EmpCorsiaVariationModification['type']> | null>;
  nonMaterialChanges: FormControl<Array<EmpCorsiaVariationModification['type']> | null>;
  reasonRegulatorLed?: FormControl<string | null>;
}

@Injectable()
export class VariationDetailsFormProvider
  implements TaskFormProvider<EmpVariationCorsiaDetails, VariationDetailsFormModel>
{
  private _form: FormGroup<VariationDetailsFormModel>;

  constructor(
    private fb: FormBuilder,
    private store: RequestTaskStore,
  ) {}

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

  get variationDetailMaterialChangesCtrl(): FormControl {
    return this.form.get('materialChanges') as FormControl;
  }

  get variationDetailNonMaterialChangesCtrl(): FormControl {
    return this.form.get('nonMaterialChanges') as FormControl;
  }

  get variationOtherChangesCtrl(): FormControl {
    return this.form.get('otherChanges') as FormControl;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(variationDetails: EmpVariationCorsiaDetails | undefined, reasonRegulatorLed?: string | undefined): void {
    this.form.patchValue({
      materialChanges:
        variationDetails?.changes?.filter((change) => Object.keys(materialChanges).includes(change)) ?? [],
      otherChanges: variationDetails?.changes?.filter((change) => Object.keys(otherChanges).includes(change)) ?? [],
      nonMaterialChanges:
        variationDetails?.changes?.filter((change) => Object.keys(nonMaterialChanges).includes(change)) ?? [],
      reason: variationDetails?.reason ?? null,
      reasonRegulatorLed: isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)
        ? (reasonRegulatorLed ?? null)
        : null,
    });
  }

  getFormValue(): EmpVariationCorsiaDetails {
    return {
      changes: this.getVariationDetailsFormValue().changes,
      reason: this.getVariationDetailsFormValue().reason,
    };
  }

  getVariationRegulatorLedReasonFormValue(): string {
    return isReasonWizardRequired(this.store.getState().requestTaskItem.requestTask.type)
      ? this.form.get('reasonRegulatorLed').value
      : null;
  }

  getVariationDetailsFormValue(): EmpVariationCorsiaDetails {
    return {
      reason: this.form.get('reason').value,
      changes: getChangesFormData(this.form.value),
    };
  }

  private _buildForm() {
    this._form = this.fb.group<VariationDetailsFormModel>(
      {
        materialChanges: new FormControl<Array<EmpCorsiaVariationModification['type']> | null>(null, {
          updateOn: 'change',
        }),
        otherChanges: new FormControl<Array<EmpCorsiaVariationModification['type']> | null>(null, {
          updateOn: 'change',
        }),
        nonMaterialChanges: new FormControl<Array<EmpCorsiaVariationModification['type']> | null>(null, {
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
              reasonRegulatorLed: new FormControl<string | null>(null, {
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
      this._form.get('reasonRegulatorLed').enable();
    }
  }
}

export function checkboxSectionsValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors => {
    const isValid =
      group.get('materialChanges').value?.length ||
      group.get('nonMaterialChanges').value?.length ||
      group.get('otherChanges').value?.length;

    return !isValid ? { invalidForm: 'Select a material or non-material or other change' } : null;
  };
}
