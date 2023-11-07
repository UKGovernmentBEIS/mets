import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AER_APPROACHES_FORM } from '../emissions';

export const dataRangeFormProvider = {
  provide: AER_APPROACHES_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions[taskKey] as CalculationOfPfcEmissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group(
      {
        fullYearCovered: [
          { value: sourceStreamEmission?.durationRange?.fullYearCovered ?? null, disabled: disabled },
          {
            validators: GovukValidators.required(
              'You must enter a date range which is either the whole year or part of the year',
            ),
          },
        ],
        coverageStartDate: new UntypedFormControl({
          value: sourceStreamEmission?.durationRange?.coverageStartDate
            ? new Date(sourceStreamEmission.durationRange.coverageStartDate)
            : null,
          disabled,
        }),
        coverageEndDate: new UntypedFormControl({
          value: sourceStreamEmission?.durationRange?.coverageEndDate
            ? new Date(sourceStreamEmission.durationRange.coverageEndDate)
            : null,
          disabled,
        }),
      },
      {
        validators: [endDateValidator()],
      },
    );
  },
};
function endDateValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors => {
    const coverageStartDate = group.get('coverageStartDate').value;
    const coverageEndDate = group.get('coverageEndDate').value;

    if (coverageEndDate) {
      if (coverageEndDate <= coverageStartDate) {
        group.controls['coverageEndDate'].setErrors({
          invalidDeadline: `The date must be the same as or before the 'Start date'.`,
        });
      } else {
        group.controls['coverageEndDate'].setErrors(null);
      }
    }
    return null;
  };
}
