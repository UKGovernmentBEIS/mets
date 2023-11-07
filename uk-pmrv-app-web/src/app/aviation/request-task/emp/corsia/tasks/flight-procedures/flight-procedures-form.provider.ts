import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { EmpFlightAndAircraftProceduresCorsia } from 'pmrv-api';

import { EmpFlightAndAircraftProceduresCorsiaFormModel } from './flight-procedures.interface';
import { ProcedureFormBuilder } from './procedure-form.builder';

@Injectable()
export class FlightProceduresFormProvider
  implements TaskFormProvider<EmpFlightAndAircraftProceduresCorsia, EmpFlightAndAircraftProceduresCorsiaFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<EmpFlightAndAircraftProceduresCorsiaFormModel>;

  private destroy$ = new Subject<void>();

  get form(): FormGroup<EmpFlightAndAircraftProceduresCorsiaFormModel> {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  get aircraftUsedDetailsCtrl(): FormGroup {
    return this.form.get('aircraftUsedDetails') as FormGroup;
  }

  get flightListCompletenessDetailsCtrl(): FormGroup {
    return this.form.get('flightListCompletenessDetails') as FormGroup;
  }

  get operatingStatePairsCtrl(): FormGroup {
    return this.form.get('operatingStatePairs') as FormGroup;
  }

  get internationalFlightsDeterminationCtrl(): FormGroup {
    return this.form.get('internationalFlightsDetermination') as FormGroup;
  }

  get internationalFlightsDeterminationNoMonitoringCtrl(): FormGroup {
    return this.form.get('internationalFlightsDeterminationNoMonitoring') as FormGroup;
  }

  get internationalFlightsDeterminationOffsetCtrl(): FormGroup {
    return this.form.get('internationalFlightsDeterminationOffset') as FormGroup;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(procedures: EmpFlightAndAircraftProceduresCorsia | undefined): void {
    if (procedures) {
      this.form.patchValue(procedures as any);
    }
  }

  getFormValue(): EmpFlightAndAircraftProceduresCorsia {
    return this.form.value as any;
  }

  private _buildForm() {
    this._form = this.fb.group({
      aircraftUsedDetails: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      flightListCompletenessDetails: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      internationalFlightsDetermination: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      internationalFlightsDeterminationNoMonitoring: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      internationalFlightsDeterminationOffset: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      operatingStatePairs: this.fb.group({
        operatingStatePairsCorsiaDetails: this.fb.control([], [Validators.required]),
      }),
    });
  }
}
