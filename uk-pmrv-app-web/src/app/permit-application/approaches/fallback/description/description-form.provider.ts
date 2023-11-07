import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const descriptionFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const value = state.permit.monitoringApproaches?.FALLBACK as FallbackMonitoringApproach;

    return fb.group({
      approachDescription: [
        { value: value?.approachDescription ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the approach description used to determine fall-back'),
          GovukValidators.maxLength(
            30000,
            'The approach description used to determine fall-back should not be more than 30000 characters',
          ),
        ],
      ],
      justification: [
        { value: value?.justification ?? null, disabled: !state.isEditable },
        [
          GovukValidators.required('Enter the justification used to determine fall-back'),
          GovukValidators.maxLength(
            10000,
            'The justification used to determine fall-back should not be more than 10000 characters',
          ),
        ],
      ],
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        value?.files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
