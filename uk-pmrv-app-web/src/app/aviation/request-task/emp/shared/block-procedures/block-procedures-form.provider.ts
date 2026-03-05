import { Injectable } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { EmpBlockOnBlockOffMethodProcedures } from 'pmrv-api';

import { ProcedureFormBuilder, ProcedureFormModel } from '../procedure-form-step';

@Injectable()
export class BlockProceduresFormProvider
  implements TaskFormProvider<EmpBlockOnBlockOffMethodProcedures, ProcedureFormModel>
{
  private _form: FormGroup<ProcedureFormModel>;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<ProcedureFormModel> {
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

  setFormValue(procedures: EmpBlockOnBlockOffMethodProcedures | undefined): void {
    if (procedures?.fuelConsumptionPerFlight) {
      this.form.patchValue(procedures.fuelConsumptionPerFlight);
    }
  }

  private buildForm() {
    return (this._form = ProcedureFormBuilder.createProcedureForm([Validators.required]));
  }
}
