import { AbstractControl, AsyncValidatorFn, UntypedFormBuilder, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
} from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { AER_CALCULATION_EMISSIONS_FORM } from '../calculation-emissions';

export const installationPostcodeFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, AerService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute, aerService: AerService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    const parameterCalculationMethod =
      sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod;

    return fb.group({
      postCode: [
        { value: parameterCalculationMethod?.postCode ?? null, disabled },
        {
          validators: [GovukValidators.required('This postcode does not exist.')],
          asyncValidators: [deliveryZoneNotExists(aerService)],
        },
      ],
    });
  },
};

function deliveryZoneNotExists(aerService: AerService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    return aerService
      .getDeliveryZones(control.value)
      .pipe(map((res) => (res?.length > 0 ? null : { postCodeNotExists: 'This postcode does not exist.' })));
  };
}
