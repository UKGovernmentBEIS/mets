import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import { atLeastTwoRequiredValidator } from './add-physical-part.component';

export const AddPhysicalPartFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const state = store.getValue();
    const value =
      state.permit.monitoringMethodologyPlans.digitizedPlan?.methodTask?.connections?.find(
        (connection) => connection.itemId == route.snapshot.paramMap.get('id'),
      ) ?? null;

    return fb.group({
      itemName: [
        {
          value: value?.itemName ?? null,
          disabled: !state.isEditable,
        },
        {
          updateOn: 'change',
          validators: [
            GovukValidators.required(
              'Enter a description of the physical part of the installation or unit that serves more than one sub-installation',
            ),
            GovukValidators.maxLength(1000, 'Enter up to 1000 characters'),
          ],
        },
      ],
      subInstallations: [
        {
          value: value?.subInstallations ?? null,
          disabled: !state.isEditable,
        },
        {
          updateOn: 'change',
          validators: [atLeastTwoRequiredValidator()],
        },
      ],
    });
  },
};
