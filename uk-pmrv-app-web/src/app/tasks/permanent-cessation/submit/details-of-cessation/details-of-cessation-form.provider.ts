import { AbstractControl, UntypedFormBuilder, ValidatorFn } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { PERMANENT_CESSATION_TASK_FORM } from '@tasks/permanent-cessation/core/permanent-cessation-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { PermanentCessationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const detailsOfPermanentCessationFormProvider = {
  provide: PERMANENT_CESSATION_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as PermanentCessationApplicationSubmitRequestTaskPayload;
    const cessationAttachments = statePayload?.permanentCessationAttachments;

    const permanentCessation = statePayload?.permanentCessation;
    const uploadSupportingFiles = permanentCessation?.files;

    return fb.group({
      description: [
        { value: permanentCessation?.description ?? null, disabled },
        {
          validators: [GovukValidators.required('Enter a description of the cessation')],
        },
      ],
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadSupportingFiles ?? [],
        cessationAttachments,
        'PERMANENT_CESSATION_UPLOAD',
        false,
        disabled,
      ),
      cessationDate: [
        { value: permanentCessation?.cessationDate ? new Date(permanentCessation?.cessationDate) : null, disabled },
        {
          validators: [todayOrPastDateValidator()],
        },
      ],
      cessationScope: [
        { value: permanentCessation?.cessationScope ?? null, disabled },
        {
          validators: GovukValidators.required(
            'Select if the cessation covers the whole installation or a sub-installation',
          ),
        },
      ],
      additionalDetails: [
        { value: permanentCessation?.additionalDetails ?? null, disabled },
        {
          validators: [GovukValidators.required('Enter the details to include in the notice document')],
        },
      ],
      regulatorComments: [{ value: permanentCessation?.regulatorComments ?? null, disabled }],
    });
  },
};

export function todayOrPastDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value >= new Date()
      ? { invalidDate: 'Date of cessation must be today or in the past' }
      : null;
  };
}
