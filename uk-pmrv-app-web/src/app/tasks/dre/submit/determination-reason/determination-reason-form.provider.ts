import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const determinationReasonFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const determinationReason = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
      ?.dre?.determinationReason;

    return fb.group({
      type: [
        { value: determinationReason?.type ?? null, disabled },
        GovukValidators.required('Say why you are determining the reportable emissions.'),
      ],
      typeOtherSummary: [
        { value: determinationReason?.typeOtherSummary ?? null, disabled },
        [
          GovukValidators.required('Say why you are determining the reportable emissions.'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      operatorAskedToResubmit: [
        { value: determinationReason?.operatorAskedToResubmit ?? false, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],
      regulatorComments: [
        { value: determinationReason?.regulatorComments ?? null, disabled },
        {
          validators: [
            GovukValidators.maxLength(10000, `The regulator comments should not be more than 10000 characters`),
          ],
        },
      ],
      supportingDocuments: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        determinationReason?.supportingDocuments ?? [],
        (store.getValue().requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
          ?.dreAttachments,
        'DRE_APPLY_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
