import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { EmpFuelUpliftMethodProcedures } from 'pmrv-api';

import { ProcedureFormBuilder, ProcedureFormModel } from '../procedure-form-step';

export interface EmpFuelUpliftMethodProceduresFormModel {
  blockHoursPerFlight: FormGroup<ProcedureFormModel>;
  zeroFuelUplift: FormControl<string>;
  fuelUpliftSupplierRecordType: FormControl<'FUEL_DELIVERY_NOTES' | 'FUEL_INVOICES'>;
  fuelDensity: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class FuelUpliftProceduresFormProvider
  implements TaskFormProvider<EmpFuelUpliftMethodProcedures, EmpFuelUpliftMethodProceduresFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<EmpFuelUpliftMethodProceduresFormModel>;
  private destroy$ = new Subject<void>();

  get form(): FormGroup<EmpFuelUpliftMethodProceduresFormModel> {
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

  setFormValue(fuelUpliftMethodProcedures: EmpFuelUpliftMethodProcedures | undefined): void {
    if (fuelUpliftMethodProcedures) {
      this.form.patchValue(fuelUpliftMethodProcedures);
    }
  }

  getFormValue(): EmpFuelUpliftMethodProcedures {
    return this.form.value as EmpFuelUpliftMethodProcedures;
  }

  get blockHoursPerFlightCtrl(): FormGroup {
    return this.form.get('blockHoursPerFlight') as FormGroup;
  }

  get zeroFuelUpliftCtrl(): FormControl {
    return this.form.get('zeroFuelUplift') as FormControl;
  }

  get fuelUpliftSupplierRecordTypeCtrl(): FormControl {
    return this.form.get('fuelUpliftSupplierRecordType') as FormControl;
  }

  get fuelDensity(): FormGroup {
    return this.form.get('fuelDensity') as FormGroup;
  }

  private buildForm() {
    this._form = this.fb.group<EmpFuelUpliftMethodProceduresFormModel>(
      {
        blockHoursPerFlight: ProcedureFormBuilder.createProcedureForm([Validators.required]),
        zeroFuelUplift: new FormControl<string | null>(null, [
          GovukValidators.required(
            'Say what data handling and calculations are necessary to meet the adjustment requirement.',
          ),
          GovukValidators.maxLength(2000, 'Enter up to 2000 characters'),
        ]),
        fuelUpliftSupplierRecordType: new FormControl<'FUEL_DELIVERY_NOTES' | 'FUEL_INVOICES' | null>(null, [
          GovukValidators.required('Say if fuel delivery notes or fuel invoices will be used as supplier records'),
        ]),
        fuelDensity: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      },
      { updateOn: 'change' },
    );
  }
}
