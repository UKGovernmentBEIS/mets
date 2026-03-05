import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { EmpEmissionsReductionClaim } from 'pmrv-api';

import { TaskFormProvider } from '../../../../task-form.provider';
import { ProcedureFormBuilder, ProcedureFormModel } from '../../../shared/procedure-form-step';

export interface EmissionsReductionClaimFormModel {
  exist: FormControl<boolean | null>;
  safMonitoringSystemsAndProcesses?: FormGroup<ProcedureFormModel>;
  rtfoSustainabilityCriteria?: FormGroup<ProcedureFormModel>;
  safDuplicationPrevention?: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class EmissionsReductionClaimFormProvider
  implements TaskFormProvider<EmpEmissionsReductionClaim, EmissionsReductionClaimFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<EmissionsReductionClaimFormModel>;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<EmissionsReductionClaimFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(emissionsReductionClaim: EmpEmissionsReductionClaim | undefined) {
    if (emissionsReductionClaim?.exist) {
      this.addProcedureForms();
    } else {
      this.removeProcedureForms();
    }

    this.form.patchValue(emissionsReductionClaim as any);
    this.existCtrl.setValue(emissionsReductionClaim?.exist ?? null);
  }

  getFormValue(): EmpEmissionsReductionClaim {
    return this.form.value as EmpEmissionsReductionClaim;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get existCtrl(): FormControl {
    return this.form.get('exist') as FormControl;
  }

  get safMonitoringSystemsAndProcessesCtrl(): FormGroup {
    return this.form.get('safMonitoringSystemsAndProcesses') as FormGroup;
  }

  get rtfoSustainabilityCriteriaCtrl(): FormGroup {
    return this.form.get('rtfoSustainabilityCriteria') as FormGroup;
  }

  get safDuplicationPreventionCtrl(): FormGroup {
    return this.form.get('safDuplicationPrevention') as FormGroup;
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        exist: new FormControl<boolean | null>(null, {
          validators: [GovukValidators.required('Select yes or no')],
          updateOn: 'change',
        }),
      },
      { updateOn: 'change' },
    );
  }

  addProcedureForms() {
    if (!this.form.contains('safMonitoringSystemsAndProcesses')) {
      this.form.addControl(
        'safMonitoringSystemsAndProcesses',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }

    if (!this.form.contains('rtfoSustainabilityCriteria')) {
      this.form.addControl(
        'rtfoSustainabilityCriteria',
        ProcedureFormBuilder.createProcedureForm([Validators.required]),
      );
    }

    if (!this.form.contains('safDuplicationPrevention')) {
      this.form.addControl('safDuplicationPrevention', ProcedureFormBuilder.createProcedureForm([Validators.required]));
    }
  }

  removeProcedureForms() {
    if (this.form.contains('safMonitoringSystemsAndProcesses')) {
      this.form.removeControl('safMonitoringSystemsAndProcesses');
    }
    if (this.form.contains('rtfoSustainabilityCriteria')) {
      this.form.removeControl('rtfoSustainabilityCriteria');
    }
    if (this.form.contains('safDuplicationPrevention')) {
      this.form.removeControl('safDuplicationPrevention');
    }
  }
}
