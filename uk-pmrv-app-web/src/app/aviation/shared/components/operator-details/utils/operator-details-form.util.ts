import { FormControl, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { parseCsv } from '@aviation/request-task/util';

import { GovukValidators } from 'govuk-components';

import { LimitedCompanyOrganisation, OrganisationStructure, PartnershipOrganisation } from 'pmrv-api';

export const organisationStructureDisableDifferentContactLocationControlByLegalStatusType = (
  organisationStructureFormGroup: UntypedFormGroup,
  legalStatusType?: OrganisationStructure['legalStatusType'],
) => {
  const differentContactLocationControl = organisationStructureFormGroup.controls.differentContactLocation;
  const differentContactLocationExistControl = organisationStructureFormGroup.controls.differentContactLocationExist
    .value as LimitedCompanyOrganisation['differentContactLocationExist'];

  organisationStructureFormGroup.controls.organisationLocation.enable();
  if (
    (['INDIVIDUAL', 'PARTNERSHIP'] as OrganisationStructure['legalStatusType'][]).includes(legalStatusType) ||
    !differentContactLocationExistControl
  ) {
    differentContactLocationControl.disable();
  } else {
    differentContactLocationControl.enable();
  }
};

export const organisationStructureDisableDifferentContactLocationControlByDifferentLocation = (
  organisationStructureFormGroup: UntypedFormGroup,
  differentContactLocationExist?: LimitedCompanyOrganisation['differentContactLocationExist'],
) => {
  const differentContactLocationControl = organisationStructureFormGroup.controls.differentContactLocation;
  if (!differentContactLocationExist) {
    differentContactLocationControl.disable();
  } else {
    differentContactLocationControl.enable();
  }
};

export const organisationStructureCreatePartnerFormControl = (partner?: string) => {
  return new FormControl(partner, {
    validators: [
      GovukValidators.required('Enter the name of partner'),
      GovukValidators.maxLength(255, 'Partner name should not be more than 255 characters'),
    ],
  });
};

export const organisationStructureCreatePartnersFormArray = (partners?: PartnershipOrganisation['partners']) => {
  return (partners || [])?.map((partner) => organisationStructureCreatePartnerFormControl(partner));
};

export const flightIdentificationRegistrationMarkingsValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const registrationMarkingsErrors = parseCsv(group.value).filter((marking) => marking.length > 20);

    return registrationMarkingsErrors.length > 0
      ? {
          invalidRegistrationMarkings: 'Code per line must not be more than 20 characters',
        }
      : null;
  };
};

export const flightIdentificationRegistrationMarkingsEmptyLineValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const registrationMarkingsErrors = group.value?.split(/\r?\n/).filter((line) => line.trim() === '');

    return registrationMarkingsErrors?.length > 0
      ? {
          invalidRegistrationMarkings: 'You cannot have empty lines in your data. Please check and try again.',
        }
      : null;
  };
};
