import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { parseCsv, unparseCsv } from '@aviation/request-task/util';
import { flightIdentificationRegistrationMarkingsValidator } from '@aviation/shared/components/operator-details/utils/operator-details-form.util';

import { GovukValidators } from 'govuk-components';

import { EmpBlockHourMethodProcedures } from 'pmrv-api';

import { ProcedureFormBuilder, ProcedureFormModel } from '../procedure-form-step';

export interface SpecificBurnFuelFormModel {
  fuelBurnCalculationTypes: FormControl<Array<'CLEAR_DISTINGUISHION' | 'NOT_CLEAR_DISTINGUISHION'>>;
  clearDistinguishionIcaoAircraftDesignators?: FormControl<string>;
  notClearDistinguishionIcaoAircraftDesignators?: FormControl<string>;
  assignmentAndAdjustment?: FormControl<string>;
}

export interface EmpBlockHourMethodProceduresFormModel {
  specificBurnFuel: FormGroup<SpecificBurnFuelFormModel>;
  blockHoursMeasurement: FormGroup<ProcedureFormModel>;
  fuelUpliftSupplierRecordType: FormControl<'FUEL_DELIVERY_NOTES' | 'FUEL_INVOICES'>;
  fuelDensity: FormGroup<ProcedureFormModel>;
}

@Injectable()
export class BlockHourProceduresFormProvider
  implements TaskFormProvider<EmpBlockHourMethodProcedures, EmpBlockHourMethodProceduresFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup<EmpBlockHourMethodProceduresFormModel>;
  private destroy$ = new Subject<void>();

  get form(): FormGroup<EmpBlockHourMethodProceduresFormModel> {
    if (!this._form) {
      this.buildForm();
    }

    this.disableSpecificBurnFuelConditionally(this._form);

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  disableSpecificBurnFuelConditionally(myform: FormGroup<EmpBlockHourMethodProceduresFormModel>) {
    const { fuelBurnCalculationTypes } = myform.value?.specificBurnFuel || {};

    if (fuelBurnCalculationTypes?.includes('CLEAR_DISTINGUISHION')) {
      myform.controls.specificBurnFuel.controls.assignmentAndAdjustment.enable();
      myform.controls.specificBurnFuel.controls.clearDistinguishionIcaoAircraftDesignators.enable();
    } else {
      myform.controls.specificBurnFuel.controls.assignmentAndAdjustment.disable();
      myform.controls.specificBurnFuel.controls.clearDistinguishionIcaoAircraftDesignators.disable();
    }

    if (fuelBurnCalculationTypes?.includes('NOT_CLEAR_DISTINGUISHION')) {
      myform.controls.specificBurnFuel.controls.notClearDistinguishionIcaoAircraftDesignators.enable();
    } else {
      myform.controls.specificBurnFuel.controls.notClearDistinguishionIcaoAircraftDesignators.disable();
    }
  }

  setFormValue(blockHourMethodProcedures: EmpBlockHourMethodProcedures | undefined): void {
    if (blockHourMethodProcedures) {
      this.form.patchValue({
        ...blockHourMethodProcedures,
      });
      this.form.controls.specificBurnFuel.patchValue({
        ...blockHourMethodProcedures,
        clearDistinguishionIcaoAircraftDesignators: blockHourMethodProcedures.clearDistinguishionIcaoAircraftDesignators
          ? unparseCsv(blockHourMethodProcedures.clearDistinguishionIcaoAircraftDesignators)
          : null,
        notClearDistinguishionIcaoAircraftDesignators:
          blockHourMethodProcedures.notClearDistinguishionIcaoAircraftDesignators
            ? unparseCsv(blockHourMethodProcedures.notClearDistinguishionIcaoAircraftDesignators)
            : null,
      });
    }
  }

  getFormValue(): EmpBlockHourMethodProcedures {
    const specificBurnFuel = { ...this.form.value.specificBurnFuel };

    const formValue = { ...this.form.value };
    delete formValue?.specificBurnFuel;
    return {
      ...formValue,
      ...specificBurnFuel,
      clearDistinguishionIcaoAircraftDesignators: specificBurnFuel.clearDistinguishionIcaoAircraftDesignators
        ? parseCsv(specificBurnFuel.clearDistinguishionIcaoAircraftDesignators, true)
        : null,
      notClearDistinguishionIcaoAircraftDesignators: specificBurnFuel.notClearDistinguishionIcaoAircraftDesignators
        ? parseCsv(specificBurnFuel.notClearDistinguishionIcaoAircraftDesignators, true)
        : null,
    } as EmpBlockHourMethodProcedures;
  }

  get specificBurnFuel(): FormGroup {
    return this.form.get('specificBurnFuel') as FormGroup;
  }

  get blockHoursMeasurement(): FormGroup {
    return this.form.get('blockHoursMeasurement') as FormGroup;
  }

  get fuelUpliftSupplierRecordType(): FormControl {
    return this.form.get('fuelUpliftSupplierRecordType') as FormControl;
  }

  get fuelDensity(): FormGroup {
    return this.form.get('fuelDensity') as FormGroup;
  }

  private buildForm() {
    this._form = this.fb.group<EmpBlockHourMethodProceduresFormModel>(
      {
        specificBurnFuel: this.fb.group<SpecificBurnFuelFormModel>({
          fuelBurnCalculationTypes: new FormControl<Array<'CLEAR_DISTINGUISHION' | 'NOT_CLEAR_DISTINGUISHION'> | null>(
            null,
            [GovukValidators.required('You must select at least 1 statement')],
          ),
          assignmentAndAdjustment: new FormControl<string | null>(null, [
            GovukValidators.required(
              'Say what data handling and calculations are necessary to meet the adjustment requirement',
            ),
            GovukValidators.maxLength(2000, 'Enter up to 2000 characters'),
          ]),
          clearDistinguishionIcaoAircraftDesignators: new FormControl<string | null>(null, [
            GovukValidators.required('State which aircraft registration markings you are using'),
            flightIdentificationRegistrationMarkingsValidator(),
          ]),
          notClearDistinguishionIcaoAircraftDesignators: new FormControl<string | null>(null, [
            GovukValidators.required('State which aircraft registration markings you are using'),
            flightIdentificationRegistrationMarkingsValidator(),
          ]),
        }),
        blockHoursMeasurement: ProcedureFormBuilder.createProcedureForm([Validators.required]),
        fuelUpliftSupplierRecordType: new FormControl<'FUEL_DELIVERY_NOTES' | 'FUEL_INVOICES' | null>(null, [
          GovukValidators.required('Say if fuel delivery notes or fuel invoices will be used as supplier records'),
        ]),
        fuelDensity: ProcedureFormBuilder.createProcedureForm([Validators.required]),
      },
      { updateOn: 'change' },
    );
  }
}
