import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { OverallVerificationAssessment } from '@shared/components/overall-decision-summary-template/overall-decision';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const overallAssessmentFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRApplicationVerificationSubmitRequestTaskPayload;
    const { type, reasons } =
      (statePayload?.verificationReport?.overallAssessment as OverallVerificationAssessment) || {};

    return fb.group(
      {
        type: [
          { value: type ?? null, disabled },
          {
            validators: [GovukValidators.required('Select your assessment of this report')],
          },
        ],
        reasons: [
          {
            value: type === 'VERIFIED_WITH_COMMENTS' ? (reasons ?? null) : null,
            disabled,
          },
        ],
        reasonsNotVerified: [
          {
            value: type === 'NOT_VERIFIED' ? (reasons ?? null) : null,
            disabled,
          },
        ],
      },
      {
        validators: reasonsValidator(),
      },
    );
  },
};

const reasonsValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const type: OverallVerificationAssessment['type'] = group.get('type').value;
    const reasons: OverallVerificationAssessment['reasons'] =
      type === 'VERIFIED_WITH_COMMENTS'
        ? group.get('reasons').value
        : type === 'NOT_VERIFIED'
          ? group.get('reasonsNotVerified').value
          : null;

    switch (type) {
      case 'VERIFIED_WITH_COMMENTS': {
        group.controls['reasonsNotVerified'].setErrors(null);

        if (!reasons) {
          group.controls['reasons'].setErrors({
            invalidReason: 'Enter a comment',
          });
        } else {
          group.controls['reasons'].setErrors(null);
        }
        break;
      }

      case 'NOT_VERIFIED': {
        group.controls['reasons'].setErrors(null);

        if (!reasons) {
          group.controls['reasonsNotVerified'].setErrors({
            invalidReason2: 'Enter a comment',
          });
        } else {
          group.controls['reasonsNotVerified'].setErrors(null);
        }
        break;
      }

      default: {
        group.controls['reasons'].setErrors(null);
        group.controls['reasonsNotVerified'].setErrors(null);
        break;
      }
    }

    return null;
  };
};
