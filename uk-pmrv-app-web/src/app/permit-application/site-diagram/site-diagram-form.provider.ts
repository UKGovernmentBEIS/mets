import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const siteDiagramAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const value = state.permit.siteDiagrams;

    return fb.group({
      siteDiagrams: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        value ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
