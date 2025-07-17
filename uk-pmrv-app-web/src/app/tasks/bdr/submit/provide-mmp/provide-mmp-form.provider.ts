import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const provideMMPFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload;
    const provideMMPFiles = statePayload.bdr?.mmpFiles;
    const bdrAttachments = statePayload?.bdrAttachments;

    return fb.group({
      hasMmp: [
        {
          value: statePayload?.bdr?.hasMmp ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select yes if you are providing a monitoring methodology plan')],
        },
      ],
      mmpFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        provideMMPFiles ?? [],
        bdrAttachments,
        'BDR_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
