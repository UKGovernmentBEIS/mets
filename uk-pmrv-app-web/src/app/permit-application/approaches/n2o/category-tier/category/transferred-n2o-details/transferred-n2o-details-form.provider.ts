import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

export const transferredN2ODetailsFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();

    const tiers = (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
      .emissionPointCategoryAppliedTiers;
    const transferredCo2 = tiers?.[index]?.emissionPointCategory?.transfer ?? null;

    return fb.group({
      installationDetailsType: [
        {
          value: transferredCo2?.installationDetailsType ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select an option'),
      ],
      emitterId: [
        {
          value: transferredCo2?.installationEmitter?.emitterId ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Enter an installation emitter ID'),
      ],
      email: [
        {
          value: transferredCo2?.installationEmitter?.email ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter your email address'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
        ],
      ],
      installationName: [
        {
          value: transferredCo2?.installationDetails?.installationName ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter an installation name'),
          GovukValidators.maxLength(255, 'Installation name should not be more than 255 characters'),
        ],
      ],
      line1: [
        {
          value: transferredCo2?.installationDetails?.line1 ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter an address'),
          GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
        ],
      ],
      line2: [
        {
          value: transferredCo2?.installationDetails?.line2 ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
      ],
      city: [
        {
          value: transferredCo2?.installationDetails?.city ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter a town or city'),
          GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
        ],
      ],
      postcode: [
        {
          value: transferredCo2?.installationDetails?.postcode ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter a postcode'),
          GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
        ],
      ],
      email2: [
        {
          value: transferredCo2?.installationDetails?.email ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter your email address'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
        ],
      ],
    });
  },
};
