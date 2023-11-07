import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { EmpMethodAProcedures } from 'pmrv-api';

import { ProcedureFormBuilder, ProcedureFormModel } from '../procedure-form-step';

export interface MethodAProceduresFormModel {
  fuelConsumptionPerFlight: FormGroup<ProcedureFormModel>;
  fuelDensity: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class MethodAProceduresFormProvider
  implements TaskFormProvider<EmpMethodAProcedures, MethodAProceduresFormModel>
{
  private _form: FormGroup<MethodAProceduresFormModel>;
  private fb = inject(FormBuilder);
  private destroy$ = new Subject<void>();

  get form(): FormGroup<MethodAProceduresFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  setFormValue(procedures: EmpMethodAProcedures | undefined): void {
    if (procedures) {
      this.form.patchValue(procedures);
    }
  }

  getFormValue(): EmpMethodAProcedures {
    return this.form.value as EmpMethodAProcedures;
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
