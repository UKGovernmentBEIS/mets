import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { TransferCO2 } from 'pmrv-api';

export function createTransferredDetailsFormGroup(transfer: Omit<TransferCO2, 'transferType'>, isEditable: boolean) {
  const disabled = !isEditable;

  return new UntypedFormGroup({
    installationDetailsType: new UntypedFormControl(
      {
        value: transfer?.installationDetailsType ?? null,
        disabled: disabled,
      },
      [GovukValidators.required('Select an option')],
    ),
    emitterId: new UntypedFormControl(
      {
        value: transfer?.installationEmitter?.emitterId ?? null,
        disabled: disabled,
      },
      [GovukValidators.required('Enter an installation emitter ID')],
    ),
    email: new UntypedFormControl(
      {
        value: transfer?.installationEmitter?.email ?? null,
        disabled: disabled,
      },
      [
        GovukValidators.required('Enter your email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      ],
    ),
    installationName: new UntypedFormControl(transfer?.installationDetails?.installationName ?? null, [
      GovukValidators.required('Enter an installation name'),
      GovukValidators.maxLength(255, 'Installation name should not be more than 255 characters'),
    ]),
    line1: new UntypedFormControl(
      {
        value: transfer?.installationDetails?.line1 ?? null,
        disabled: disabled,
      },
      [
        GovukValidators.required('Enter an address'),
        GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
      ],
    ),
    line2: new UntypedFormControl(
      {
        value: transfer?.installationDetails?.line2 ?? null,
        disabled: disabled,
      },
      [GovukValidators.maxLength(255, 'The address should not be more than 255 characters')],
    ),
    city: new UntypedFormControl(
      {
        value: transfer?.installationDetails?.city ?? null,
        disabled: disabled,
      },
      [
        GovukValidators.required('Enter a town or city'),
        GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
      ],
    ),
    postcode: new UntypedFormControl(
      {
        value: transfer?.installationDetails?.postcode ?? null,
        disabled: disabled,
      },
      [
        GovukValidators.required('Enter a post code'),
        GovukValidators.maxLength(64, 'The post code should not be more than 64 characters'),
      ],
    ),
    email2: new UntypedFormControl(
      {
        value: transfer?.installationDetails?.email ?? null,
        disabled: disabled,
      },
      [
        GovukValidators.required('Enter your email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Enter an email address with a maximum of 255 characters'),
      ],
    ),
  });
}
