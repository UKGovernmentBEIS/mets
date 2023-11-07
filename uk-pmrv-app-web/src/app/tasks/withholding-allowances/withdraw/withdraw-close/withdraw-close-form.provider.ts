import { UntypedFormBuilder } from '@angular/forms';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../shared/services/request-task-file-service/request-task-file.service';
import { WITHHOLDING_ALLOWANCES_TASK_FORM } from '../../core/withholding-allowances';

export const withdrawCloseReasonFormProvider = {
  provide: WITHHOLDING_ALLOWANCES_TASK_FORM,

  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.value;
    const disabled = !state.isEditable;

    return fb.group({
      reason: [
        { value: null, disabled },
        {
          validators: [
            GovukValidators.required('You must give an explanation'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        [],
        (
          store.getValue()?.requestTaskItem?.requestTask
            ?.payload as WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload
        )?.withholdingWithdrawalAttachments,
        'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
