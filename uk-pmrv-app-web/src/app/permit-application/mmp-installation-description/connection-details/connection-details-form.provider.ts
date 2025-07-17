import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukValidators } from 'govuk-components';

import { InstallationConnection } from 'pmrv-api';

export const connectionsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
  ) => {
    const connection =
      store.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.['connections']?.[
        route.snapshot.paramMap.get('connectionIndex')
      ] ?? null;

    const formGroup = fb.group({
      installationOrEntityName: [
        connection?.installationOrEntityName ?? null,
        [
          GovukValidators.required('Enter the installation or entity name'),
          GovukValidators.maxLength(255, 'The name should not be larger than 255 characters'),
        ],
      ],
      entityType: [
        connection?.entityType ?? null,
        { validators: GovukValidators.required('Select the installation or entity type'), updateOn: 'change' },
      ],

      connectionType: [connection?.connectionType ?? null, GovukValidators.required('Select the connection type')],
      flowDirection: [connection?.flowDirection ?? null, GovukValidators.required('Select the flow direction')],

      installationId: [
        connection?.installationId ?? null,
        connection?.entityType == 'ETS_INSTALLATION' || connection?.entityType == 'NITRIC_ACID_PRODUCTION'
          ? [
              GovukValidators.required('Enter the installation ID'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]
          : [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      contactPersonName: [
        connection?.contactPersonName ?? null,
        [
          GovukValidators.required('Enter a name'),
          GovukValidators.maxLength(255, 'The contact name should not be larger than 255 characters'),
        ],
      ],
      emailAddress: [
        connection?.emailAddress ?? null,
        [
          GovukValidators.required('Enter an email address'),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
        ],
      ],
      phoneNumber: [
        connection?.phoneNumber ?? null,
        [
          GovukValidators.required('Enter a phone number'),
          GovukValidators.maxLength(255, 'The telephone number should not be larger than 255 characters'),
        ],
      ],
    });

    formGroup.get('entityType').valueChanges.subscribe((entityType) => {
      if (entityType == 'ETS_INSTALLATION' || entityType == 'NITRIC_ACID_PRODUCTION') {
        formGroup.controls.installationId.clearValidators();
        formGroup.controls.installationId.setValidators([
          GovukValidators.required('Enter the installation ID'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      } else {
        formGroup.controls.installationId.clearValidators();
        formGroup.controls.installationId.setValidators([
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ]);
      }
      formGroup.controls.installationId.updateValueAndValidity();
    });

    return formGroup;
  },
};

export const entityTypeOptions: InstallationConnection['entityType'][] = [
  'ETS_INSTALLATION',
  'NON_ETS_INSTALLATION',
  'NITRIC_ACID_PRODUCTION',
  'HEAT_DISTRIBUTION_NETWORK',
];
export const connectionTypeOptions: InstallationConnection['connectionType'][] = [
  'MEASURABLE_HEAT',
  'WASTE_GAS',
  'TRANSFERRED_CO2_FOR_USE_IN_YOUR_INSTALLATION_CCU',
  'TRANSFERRED_CO2_FOR_STORAGE',
  'INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS',
];
export const flowDirectionOptions: InstallationConnection['flowDirection'][] = ['IMPORT', 'EXPORT'];
