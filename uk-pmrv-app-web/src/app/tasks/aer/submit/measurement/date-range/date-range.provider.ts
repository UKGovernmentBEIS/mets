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

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const dataRangeFormProvider = {
  provide: AER_MEASUREMENT_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();

    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const emissionPointEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group(
      {
        fullYearCovered: [
          { value: emissionPointEmission?.durationRange?.fullYearCovered ?? null, disabled: disabled },
          {
            validators: GovukValidators.required(
              'You must enter a date range which is either the whole year or part of the year',
            ),
          },
        ],
        coverageStartDate: new UntypedFormControl({
          value: emissionPointEmission?.durationRange?.coverageStartDate
            ? new Date(emissionPointEmission.durationRange.coverageStartDate)
            : null,
          disabled,
        }),
        coverageEndDate: new UntypedFormControl({
          value: emissionPointEmission?.durationRange?.coverageEndDate
            ? new Date(emissionPointEmission.durationRange.coverageEndDate)
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
