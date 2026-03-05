import { Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { AviationAerCorsiaProcessAnalysis } from 'pmrv-api';

export interface AviationAerCorsiaProcessAnalysisFormModel {
  verificationActivities: FormControl<string | null>;
  strategicAnalysis: FormControl<string | null>;
  dataSampling: FormControl<string | null>;
  dataSamplingResults: FormControl<string | null>;
  emissionsMonitoringPlanCompliance: FormControl<string | null>;
}

@Injectable()
export class ProcessAnalysisFormProvider
  implements TaskFormProvider<AviationAerCorsiaProcessAnalysis, AviationAerCorsiaProcessAnalysisFormModel>
{
  private _form: FormGroup<AviationAerCorsiaProcessAnalysisFormModel>;

  constructor(private fb: FormBuilder) {}

  get verificationActivitiesCtrl(): FormControl {
    return this.form.get('verificationActivities') as FormControl;
  }

  get strategicAnalysisCtrl(): FormControl {
    return this.form.get('strategicAnalysis') as FormControl;
  }

  get dataSamplingCtrl(): FormControl {
    return this.form.get('dataSampling') as FormControl;
  }

  get dataSamplingResultsCtrl(): FormControl {
    return this.form.get('dataSamplingResults') as FormControl;
  }

  destroyForm(): void {
    this._form = null;
  }

  get form(): FormGroup<AviationAerCorsiaProcessAnalysisFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getFormValue(): AviationAerCorsiaProcessAnalysis {
    return this.form.getRawValue();
  }

  setFormValue(formValue: AviationAerCorsiaProcessAnalysis): void {
    this.form.setValue({
      verificationActivities: formValue?.verificationActivities ?? null,
      strategicAnalysis: formValue?.strategicAnalysis ?? null,
      dataSampling: formValue?.dataSampling ?? null,
      dataSamplingResults: formValue?.dataSamplingResults ?? null,
      emissionsMonitoringPlanCompliance: formValue?.emissionsMonitoringPlanCompliance ?? null,
    });
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        verificationActivities: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Describe the verification activities and results'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        strategicAnalysis: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Enter the results of your strategic analysis and risk assessment'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        dataSampling: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Describe the data sampling and testing procedures'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        dataSamplingResults: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('Give the results of all the data sampling and testing exercises'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
        emissionsMonitoringPlanCompliance: new FormControl<string>(null, {
          validators: [
            GovukValidators.required(
              'Confirm if the monitoring was performed according to the emissions monitoring plan or not',
            ),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }
}
