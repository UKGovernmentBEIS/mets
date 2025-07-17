import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { INSPECTION_TASK_FORM, InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const followUpActionGuardFormProvider = {
  provide: INSPECTION_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ): UntypedFormGroup => {
    const state = store.value;
    const disabled = !state.isEditable;
    const type = route.snapshot.paramMap.get('type');

    const statePayload = state.requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload;
    const files = statePayload?.installationInspection?.followUpActionsOmissionFiles;

    return fb.group({
      followUpActionsRequired: [
        {
          value: statePayload?.installationInspection?.followUpActionsRequired,
          disabled,
        },
        { validators: [GovukValidators.required('Select yes if you want to add follow-up actions for the operator')] },
      ],
      followUpActionsOmissionJustification: [
        {
          value: statePayload?.installationInspection?.followUpActionsOmissionJustification,
          disabled,
        },
      ],
      followUpActionsOmissionFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        files ?? [],
        statePayload?.inspectionAttachments,
        type === 'audit' ? 'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT' : 'INSTALLATION_ONSITE_INSPECTION_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
