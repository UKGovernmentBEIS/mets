import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { MonitoringRole } from 'pmrv-api';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const monitoringRolesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const value = store.permit.monitoringReporting;

    return fb.group({
      monitoringRoles: fb.array(
        value?.monitoringRoles.length > 0
          ? value.monitoringRoles.map((v) => createAnotherRole(v, !store.getValue().isEditable))
          : [createAnotherRole(null, !store.getValue().isEditable)],
      ),
      organisationCharts: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        value?.organisationCharts ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !store.getValue().isEditable,
      ),
    });
  },
};

export function createAnotherRole(value?: MonitoringRole, disabled = false): UntypedFormGroup {
  return new UntypedFormGroup({
    jobTitle: new UntypedFormControl({ value: value?.jobTitle ?? null, disabled }, [
      GovukValidators.required('Enter a job title'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    mainDuties: new UntypedFormControl({ value: value?.mainDuties ?? null, disabled }, [
      GovukValidators.required('Enter the main duties'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
