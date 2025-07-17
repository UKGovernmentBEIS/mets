import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { permitRevocationMapper } from '@permit-revocation/constants/permit-revocation-consts';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { addDays, endOfDay, format, isAfter, isBefore, isEqual, isValid, startOfDay, subDays } from 'date-fns';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

import { PermitRevocation } from 'pmrv-api';

export const PERMIT_REVOCATION_TASK_FORM = new InjectionToken<UntypedFormGroup>('Permit revocation task form');

let effectiveDate: string;

export const permitRevocationFormProvider = {
  provide: PERMIT_REVOCATION_TASK_FORM,
  deps: [UntypedFormBuilder, PermitRevocationStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: PermitRevocationStore, route: ActivatedRoute) => {
    const keys: string[] = route.snapshot.data.keys;
    const skipValidators = route.snapshot.data.skipValidators;
    const formGroupObj = {};

    const state = store.getValue();
    const permitRevocation = state.permitRevocation;
    const disabled = !state.isEditable;
    effectiveDate = permitRevocation?.effectiveDate;

    for (const key of keys) {
      if (key) {
        formGroupObj[key] = [
          {
            value:
              permitRevocation !== undefined && permitRevocation[key] !== undefined && permitRevocation[key] !== null
                ? value(permitRevocation, key)
                : null,
            disabled,
          },
          { validators: skipValidators ? [] : addValidators(key) },
        ];
      }
    }
    return fb.group(formGroupObj);
  },
};

export const effectiveDateMinValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const after28Days = startOfDay(addDays(new Date(), 28));
    const effectiveDate = group.value?.effectiveDate ? group.value.effectiveDate : group.value;

    return isAfter(effectiveDate, after28Days)
      ? null
      : {
          invalidEffectiveDate: `The effective date of the notice must be at least 28 days after today`,
        };
  };
};

export const feeDateMinValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const govukDatePipe = new GovukDatePipe();
    const currentEffectiveDate = new Date(effectiveDate);
    const errorProperty = permitRevocationMapper['feeDate'].error;

    const feeDate = group.value?.feeDate ? group.value.feeDate : group.value;

    return isAfter(feeDate, currentEffectiveDate)
      ? null
      : {
          [errorProperty]: `The date must be after ${govukDatePipe.transform(
            format(currentEffectiveDate, 'yyyy-MM-dd'),
          )}`,
        };
  };
};

export const stoppedDateMaxDateValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const maxDate = subDays(endOfDay(new Date()), 1);
    const errorProperty = permitRevocationMapper['stoppedDate'].error;
    const stoppedDate = group.value ? group.value.stoppedDate || group.value : null;

    return !stoppedDate ||
      stoppedDate.stoppedDate === null ||
      isBefore(stoppedDate, maxDate) ||
      isEqual(stoppedDate, maxDate)
      ? null
      : {
          [errorProperty]: `The date must be in the past`,
        };
  };
};

export const genericMinDateValidator = (property: string): ValidatorFn => {
  const govukDatePipe = new GovukDatePipe();
  return (group: UntypedFormGroup): ValidationErrors => {
    const minDate = startOfDay(new Date());
    const errorProperty = permitRevocationMapper[property].error;
    const fieldValue = !group.value ? null : group.value[property] || group.value;

    return !fieldValue || !isValid(fieldValue) || isAfter(fieldValue, minDate) || isEqual(fieldValue, minDate)
      ? null
      : {
          [errorProperty]: `This date must be the same as or after ${govukDatePipe.transform(
            format(minDate, 'yyyy-MM-dd'),
          )}`,
        };
  };
};

const value = (permitRevocation: PermitRevocation, key: string) => {
  return key.toLowerCase().includes('date') ? new Date(permitRevocation[key]) : permitRevocation[key];
};

const addValidators = (key: string): MessageValidatorFn[] => {
  switch (key) {
    case 'activitiesStopped':
      return [GovukValidators.required('Select yes or no')];
    case 'stoppedDate':
      return [stoppedDateMaxDateValidator()];
    case 'reason':
      return [GovukValidators.required('Explain the reason why the permit is being revoked')];
    case 'effectiveDate':
      return [effectiveDateMinValidator()];
    case 'annualEmissionsReportRequired':
      return [GovukValidators.required('Select yes or no')];
    case 'annualEmissionsReportDate':
      return [genericMinDateValidator('annualEmissionsReportDate')];
    case 'surrenderRequired':
      return [GovukValidators.required('Select yes or no')];
    case 'surrenderDate':
      return [genericMinDateValidator('surrenderDate')];
    case 'feeCharged':
      return [GovukValidators.required('Select yes or no')];
    case 'feeDate':
      return [feeDateMinValidator()];
    case 'feeDetails':
      return [GovukValidators.required('Explain why payment is required')];
    default:
      return null;
  }
};
