import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AerVerify, AerVerifyCorsia, RequestTaskStore } from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

export const AER_VERIFY_TASK_FORM = new InjectionToken<UntypedFormGroup>('Aer verify task form');

export const MisstatementItemFormProvider = {
  provide: AER_VERIFY_TASK_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const misstatementInfo = (state.requestTaskItem.requestTask.payload as AerVerify | AerVerifyCorsia)
      .verificationReport.uncorrectedMisstatements;

    const item =
      index < misstatementInfo?.uncorrectedMisstatements?.length
        ? misstatementInfo.uncorrectedMisstatements[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Add a description of the misstatement'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
      materialEffect: [
        { value: item?.materialEffect, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(
              'Select if this misstatement has a material effect on the total emissions reported',
            ),
          ],
        },
      ],
    });
  },
};
