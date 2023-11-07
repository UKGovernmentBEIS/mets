import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AerVerify, AerVerifyCorsia, RequestTaskStore } from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

export const AER_VERIFY_TASK_FORM = new InjectionToken<UntypedFormGroup>('Aer verify task form');

export const RecommendedImprovementsItemFormProvider = {
  provide: AER_VERIFY_TASK_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const improvementsInfo = (state.requestTaskItem.requestTask.payload as AerVerify | AerVerifyCorsia)
      .verificationReport.recommendedImprovements;

    const item =
      index < improvementsInfo?.recommendedImprovements?.length
        ? improvementsInfo.recommendedImprovements[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Add a recommended improvement for the aircraft operator'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
    });
  },
};
