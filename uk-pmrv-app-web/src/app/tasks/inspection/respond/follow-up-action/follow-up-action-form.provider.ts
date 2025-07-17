import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { INSPECTION_TASK_FORM } from '@tasks/inspection/shared/inspection';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { InstallationInspectionOperatorRespondRequestTaskPayload } from 'pmrv-api';

export const followUpActionRespondFormProvider = {
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
    const id = route.snapshot.paramMap.get('actionId');
    const payload = state.requestTaskItem.requestTask
      .payload as InstallationInspectionOperatorRespondRequestTaskPayload;
    const actionResponse = payload?.followupActionsResponses[id];
    const responseAttachments = actionResponse?.followUpActionResponseAttachments;

    return fb.group(
      {
        completed: [
          {
            value: actionResponse?.completed,
            disabled,
          },
          { validators: [GovukValidators.required('Select an option')] },
        ],
        explanationTrue: [
          { value: actionResponse?.completed ? actionResponse?.explanation : null, disabled },
          {
            validators: [
              GovukValidators.required('Enter an explanation'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ],
          },
        ],
        explanationFalse: [
          { value: !actionResponse?.completed ? actionResponse?.explanation : null, disabled },
          {
            validators: [
              GovukValidators.required('Enter an explanation'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ],
          },
        ],
        completionDate: [
          {
            value: actionResponse?.completionDate ? new Date(actionResponse?.completionDate) : null,
            disabled,
          },
        ],
        followUpActionResponseAttachmentsTrue: requestTaskFileService.buildFormControl(
          store.requestTaskId,
          actionResponse?.completed ? responseAttachments : [],
          payload?.inspectionAttachments,
          type === 'audit'
            ? 'INSTALLATION_AUDIT_OPERATOR_RESPOND_UPLOAD_ATTACHMENT'
            : 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_UPLOAD_ATTACHMENT',
          false,
          disabled,
        ),
        followUpActionResponseAttachmentsFalse: requestTaskFileService.buildFormControl(
          store.requestTaskId,
          !actionResponse?.completed ? responseAttachments : [],
          payload?.inspectionAttachments,
          type === 'audit'
            ? 'INSTALLATION_AUDIT_OPERATOR_RESPOND_UPLOAD_ATTACHMENT'
            : 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_UPLOAD_ATTACHMENT',
          false,
          disabled,
        ),
      },
      { updateOn: 'change' },
    );
  },
};
