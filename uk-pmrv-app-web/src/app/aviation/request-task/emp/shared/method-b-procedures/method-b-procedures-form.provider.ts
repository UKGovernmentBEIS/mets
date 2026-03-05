import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { EmpMethodBProcedures } from 'pmrv-api';

import { ProcedureFormBuilder, ProcedureFormModel } from '../procedure-form-step';

export interface MethodBProceduresFormModel {
  fuelConsumptionPerFlight: FormGroup<ProcedureFormModel>;
  fuelDensity: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class MethodBProceduresFormProvider
  implements TaskFormProvider<EmpMethodBProcedures, MethodBProceduresFormModel>
{
  private _form: FormGroup<MethodBProceduresFormModel>;
  private fb = inject(FormBuilder);
  private destroy$ = new Subject<void>();

  get form(): FormGroup<MethodBProceduresFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(procedures: EmpMethodBProcedures | undefined): void {
    if (procedures) {
      this.form.patchValue(procedures);
    }
  }

  getFormValue(): EmpMethodBProcedures {
    return this.form.value as EmpMethodBProcedures;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  get fuelConsumptionPerFlightCtrl(): FormGroup {
    return this.form.get('fuelConsumptionPerFlight') as FormGroup;
  }

  get fuelDensityCtrl(): FormGroup {
    return this.form.get('fuelDensity') as FormGroup;
  }

  private buildForm() {
    return (this._form = this.fb.group(
      {
        fuelConsumptionPerFlight: ProcedureFormBuilder.createProcedureForm([Validators.required]),
        fuelDensity: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      },
      { updateOn: 'change' },
    ));
  }
}
