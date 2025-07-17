import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { DirectlyAttributableEmissionsFA } from 'pmrv-api';

import { isFallbackApproach } from '../../mmp-sub-installations-status';

export const directlyAttributableEmissionsAddFormFactory = {
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
        attribution: [
          (fallbackApproach?.directlyAttributableEmissions as DirectlyAttributableEmissionsFA)?.attribution ?? null,
          ['HEAT_BENCHMARK_CL', 'HEAT_BENCHMARK_NON_CL', 'DISTRICT_HEATING_NON_CL'].includes(
            fallbackApproach?.subInstallationType,
          )
            ? [GovukValidators.maxLength(15000, 'Enter up to 15000 characters')]
            : [
                GovukValidators.required('Explain how directly attributable emissions are attributed'),
                GovukValidators.maxLength(15000, 'Enter up to 15000 characters'),
              ],
        ],

        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          (fallbackApproach?.directlyAttributableEmissions as DirectlyAttributableEmissionsFA)?.supportingFiles ?? [],
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
