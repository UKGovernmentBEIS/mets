import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AerVerify, AerVerifyCorsia, RequestTaskStore } from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

export const AER_VERIFY_TASK_FORM = new InjectionToken<UntypedFormGroup>('Aer verify task form');

export const reasonItemFormProvider = {
  provide: AER_VERIFY_TASK_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const overallAssessmentInfo = (state.requestTaskItem.requestTask.payload as AerVerify | AerVerifyCorsia)
      .verificationReport.overallDecision as AviationAerVerifiedSatisfactoryWithCommentsDecision;

    const item = index < overallAssessmentInfo?.reasons?.length ? overallAssessmentInfo.reasons[index] : null;

    return fb.group({
      reason: [
        { value: item, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Give a reason why the report cannot be verified as satisfactory'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
    });
  },
};
