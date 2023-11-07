import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const uploadMonitoringMethodologyFileProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ): UntypedFormGroup => {
    const state = store.getValue();
    const files = state.permit?.monitoringMethodologyPlans?.plans;

    const disabled = !state.isEditable;

    return fb.group({
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        true,
        disabled,
      ),
    });
  },
};
