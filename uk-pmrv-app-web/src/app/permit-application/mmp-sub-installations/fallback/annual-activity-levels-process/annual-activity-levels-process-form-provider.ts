import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { isFallbackApproach } from '@permit-application/mmp-sub-installations/mmp-sub-installations-status';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

export const annualActivityProcessAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const fallbackApproach = store.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations
      ?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
      ?.find((product) => product.subInstallationNo === route.snapshot.paramMap.get('subInstallationNo'));

    const formGroup = fb.group(
      {
        methodologyAppliedDescription: [
          fallbackApproach?.annualLevel?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the methodology used'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        trackingMethodologyDescription: [
          fallbackApproach?.annualLevel?.trackingMethodologyDescription ?? null,
          [
            GovukValidators.required('Enter a description of the methodology used to track the products produced'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          fallbackApproach?.annualLevel?.supportingFiles ?? [],
          store.getState().permitAttachments,
          store.getFileUploadSectionAttachmentActionContext(),
          false,
          !state.isEditable,
        ),
      },
      {
        updateOn: 'change',
      },
    );

    return formGroup;
  },
};
