import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaVerifiersConclusions } from 'pmrv-api';

export interface AviationAerCorsiaVerifiersConclusionsFormModel {
  dataQualityMateriality: FormControl<string | null>;
  materialityThresholdType: FormControl<AviationAerCorsiaVerifiersConclusions['materialityThresholdType'] | null>;
  materialityThresholdMet: FormControl<boolean | null>;
  emissionsReportConclusion: FormControl<string | null>;
}

@Injectable()
export class VerifiersConclusionsFormProvider
  implements TaskFormProvider<AviationAerCorsiaVerifiersConclusions, AviationAerCorsiaVerifiersConclusionsFormModel>
{
  private _form: FormGroup<AviationAerCorsiaVerifiersConclusionsFormModel>;

  constructor(private fb: FormBuilder) {}

  get dataQualityMaterialityCtrl(): FormControl {
    return this.form.get('dataQualityMateriality') as FormControl;
  }

  get materialityThresholdTypeCtrl(): FormControl {
    return this.form.get('materialityThresholdType') as FormControl;
  }

  get materialityThresholdMetCtrl(): FormControl {
    return this.form.get('materialityThresholdMet') as FormControl;
  }

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaVerifiersConclusionsFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaVerifiersConclusions {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaVerifiersConclusions): void {
    this.form.setValue({
      dataQualityMateriality: formValue?.dataQualityMateriality ?? null,
      materialityThresholdType: formValue?.materialityThresholdType ?? null,
      materialityThresholdMet: formValue?.materialityThresholdMet ?? null,
      emissionsReportConclusion: formValue?.emissionsReportConclusion ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        dataQualityMateriality: new FormControl<string>(null, {
          validators: [
            GovukValidators.required(`Provide your conclusions on the overall quality of the operator's data`),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        materialityThresholdType: new FormControl<AviationAerCorsiaVerifiersConclusions['materialityThresholdType']>(
          null,
          {
            validators: [GovukValidators.required('Enter the materiality threshold for this aeroplane operator')],
          },
        ),
        materialityThresholdMet: new FormControl<boolean>(null, {
          validators: [
            GovukValidators.required('Select if the materiality threshold is being met in the emissions report'),
          ],
        }),
        emissionsReportConclusion: new FormControl<string>(null, {
          validators: [
            GovukValidators.required(`Provide your conclusions on the operator's emissions report`),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
