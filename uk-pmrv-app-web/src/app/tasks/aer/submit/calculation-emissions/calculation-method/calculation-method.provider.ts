import { AbstractControl, AsyncValidatorFn, UntypedFormBuilder, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable, of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationParameterCalculationMethod,
  ReportingDataService,
} from 'pmrv-api';

import { AER_CALCULATION_EMISSIONS_FORM } from '../calculation-emissions';

export const calculationMethodFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, ReportingDataService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    reportingDataService: ReportingDataService,
  ) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group(
      {
        type: [
          { value: sourceStreamEmission?.parameterCalculationMethod?.type ?? null, disabled },
          {
            validators: GovukValidators.required('You must choose a data calculation method'),
          },
        ],
      },
      {
        updateOn: 'change',
        asyncValidators: [validateInventoryDataYearExistence(`${payload.reportingYear}`, reportingDataService)],
      },
    );
  },
};

function validateInventoryDataYearExistence(
  year: string,
  reportingDataService: ReportingDataService,
): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    const parameterCalculationMethod = control.get('type').value;

    if (!parameterCalculationMethod || parameterCalculationMethod === 'MANUAL') {
      return of(null);
    } else {
      const inventoryCalculationMethodType = findInventoryCalculationMethodType(control.get('type').value);

      return reportingDataService.getInventoryDataExistenceByYear(year, inventoryCalculationMethodType).pipe(
        first(),
        map((inventoryDataYearExistenceDTO) =>
          inventoryDataYearExistenceDTO.exist
            ? null
            : {
                noExistence: `The ${inventoryCalculationMethodType.toLowerCase()} inventory data set for the current reporting year has not yet been uploaded in the system. You should proceed by calculating the values manually.`,
              },
        ),
      );
    }
  };
}

function findInventoryCalculationMethodType(
  type: CalculationParameterCalculationMethod['type'],
): 'NATIONAL' | 'REGIONAL' {
  switch (type) {
    case 'REGIONAL_DATA':
      return 'REGIONAL';
    case 'NATIONAL_INVENTORY_DATA':
      return 'NATIONAL';
    default:
      return null;
  }
}
