import { UntypedFormBuilder } from '@angular/forms';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

export const MmpFlowDiagramProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();

    const files = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.flowDiagrams;

    return fb.group({
      flowDiagrams: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        true,
        !state.isEditable,
      ),
    });
  },
};
