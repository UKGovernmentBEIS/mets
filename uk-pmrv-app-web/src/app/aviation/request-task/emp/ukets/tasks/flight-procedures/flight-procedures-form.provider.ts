import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { ProcedureFormBuilder, ProcedureFormModel } from '@aviation/request-task/emp/shared/procedure-form-step';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { EmpFlightAndAircraftProcedures } from 'pmrv-api';

export interface FlightProceduresFormModel {
  aircraftUsedDetails: FormGroup<ProcedureFormModel>;
  flightListCompletenessDetails: FormGroup<ProcedureFormModel>;
  ukEtsFlightsCoveredDetails: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class FlightProceduresFormProvider
  implements TaskFormProvider<EmpFlightAndAircraftProcedures, FlightProceduresFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<FlightProceduresFormModel>;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<FlightProceduresFormModel> {
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

  setFormValue(procedures: EmpFlightAndAircraftProcedures | undefined): void {
    if (procedures) {
      this.form.patchValue(procedures);
    }
  }

  getFormValue(): EmpFlightAndAircraftProcedures {
    return this.form.value as EmpFlightAndAircraftProcedures;
  }

  get aircraftUsedDetailsCtrl(): FormGroup {
    return this.form.get('aircraftUsedDetails') as FormGroup;
  }

  get flightListCompletenessDetails(): FormGroup {
    return this.form.get('flightListCompletenessDetails') as FormGroup;
  }

  get ukEtsFlightsCoveredDetails(): FormGroup {
    return this.form.get('ukEtsFlightsCoveredDetails') as FormGroup;
  }

  private buildForm() {
    return (this._form = this.fb.group(
      {
        aircraftUsedDetails: ProcedureFormBuilder.createProcedureForm([Validators.required]),
        flightListCompletenessDetails: ProcedureFormBuilder.createProcedureForm([Validators.required]),
        ukEtsFlightsCoveredDetails: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      },
      { updateOn: 'change' },
    ));
  }
}
