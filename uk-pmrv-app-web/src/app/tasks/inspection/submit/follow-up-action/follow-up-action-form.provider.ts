import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { INSPECTION_TASK_FORM, InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { FollowUpAction } from 'pmrv-api';

export const followUpActionSubmitFormProvider = {
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
    const id = route.snapshot.paramMap.get('id');
    const followUpAction = (state.requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload)
      ?.installationInspection?.followUpActions?.[id] as FollowUpAction;
    const followUpActionAttachments = followUpAction?.followUpActionAttachments;

    const statePayload = state.requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload;

    return fb.group({
      followUpActionType: [
        {
          value: followUpAction?.followUpActionType,
          disabled,
        },
        { validators: [GovukValidators.required('Enter an action type')] },
      ],
      explanation: [
        { value: followUpAction?.explanation, disabled },
        {
          validators: [
            GovukValidators.required('Enter an explanation'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
      followUpActionAttachments: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        followUpActionAttachments ?? [],
        statePayload?.inspectionAttachments,
        type === 'audit' ? 'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT' : 'INSTALLATION_ONSITE_INSPECTION_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
