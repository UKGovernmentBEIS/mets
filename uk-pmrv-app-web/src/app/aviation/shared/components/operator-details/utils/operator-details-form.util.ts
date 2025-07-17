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

export const flightIdentificationTypeValidator = [GovukValidators.required('Select the call sign identifier you use')];

export const icaoDesignatorsValidator = [
  GovukValidators.required('State which ICAO designators you are using'),
  GovukValidators.maxLength(100, 'The list of ICAO designators should not be more than 100 characters'),
];

export const aircraftRegistrationMarkingsValidator = [
  GovukValidators.required('State which aircraft registration markings you are using'),
  flightIdentificationRegistrationMarkingsValidator(),
  flightIdentificationRegistrationMarkingsEmptyLineValidator(),
];

export const certificateExistValidator = [
  GovukValidators.required('Select if you have an Air Operating Certificate or equivalent certification'),
];

export const certificateNumberValidator = [
  GovukValidators.required('Enter a certificate number'),
  GovukValidators.maxLength(255, 'Certificate number should not be more than 255 characters'),
];

export const issuingAuthorityValidator = [GovukValidators.required('Select an issuing authority')];

export const restrictionsExistValidator = [GovukValidators.required('Select yes or no')];

export const restrictionsDetailsValidator = [
  GovukValidators.required('Enter more information'),
  GovukValidators.maxLength(500, 'The information you provide should not be more than 500 characters'),
];

export const legalStatusTypeValidator = [
  GovukValidators.required('Select the option which shows the legal status of your organisation'),
];

export const registrationNumberValidator = [
  GovukValidators.required('Enter the company registration number'),
  GovukValidators.maxLength(40, 'Registration number should not be more than 40 characters'),
];

export const differentContactLocationExistValidator = [
  GovukValidators.required('Say if you would like to enter a different contact address'),
];

export const fullNameValidator = [
  GovukValidators.required('Enter full name'),
  GovukValidators.maxLength(255, 'Full name should not be more than 255 characters'),
];

export const partnersValidator = [GovukValidators.required('Enter the name of the partner')];

export const operatorTypeValidator = [
  GovukValidators.required('Select if you are a commercial or non-commercial operator'),
];

export const flightTypesValidator = [
  GovukValidators.required('Select if you operate scheduled or non-scheduled flights'),
];

export const activityDescriptionValidator = [
  GovukValidators.required('Describe what kind of activities you carry out'),
  GovukValidators.maxLength(10000, 'The activities you describe should not be more than 10000 characters'),
];

export const operatorNameValidator = [
  GovukValidators.required('Enter an aeroplane or aircraft operator name'),
  GovukValidators.maxLength(255, 'Enter a valid aeroplane or aircraft operator name'),
];

export const line1Validator = [GovukValidators.required('Enter the first line of your address')];

export const cityValidator = [GovukValidators.required('Enter your town or city')];

export const countryValidator = [GovukValidators.required('Enter your country')];

export const subsidiaryCompanyExistValidator = [
  GovukValidators.required(
    'Select if you want to be regulated as a single aeroplane operator with a wholly-owned subsidiary',
  ),
];
