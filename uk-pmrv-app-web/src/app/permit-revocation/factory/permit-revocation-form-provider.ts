import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { permitRevocationMapper } from '@permit-revocation/constants/permit-revocation-consts';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import moment from 'moment';

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
    const after28Days = moment().add(28, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    return moment(group.value?.effectiveDate ? group.value.effectiveDate : group.value).isAfter(after28Days)
      ? null
      : {
          invalidEffectiveDate: `The effective date of the notice must be at least 28 days after today`,
        };
  };
};

export const feeDateMinValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const govukDatePipe = new GovukDatePipe();
    const currentEffectiveDate = moment(new Date(effectiveDate));
    const errorProperty = permitRevocationMapper['feeDate'].error;

    return moment(group.value?.feeDate ? group.value.feeDate : group.value).isAfter(currentEffectiveDate)
      ? null
      : {
          [errorProperty]: `The date must be after ${govukDatePipe.transform(
            new Date(currentEffectiveDate.format('YYYY-MM-DD')),
          )}`,
        };
  };
};

export const stoppedDateMaxDateValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const maxDate = moment().set({ hour: 23, minute: 59, second: 59, millisecond: 59 }).subtract(1, 'days');
    const errorProperty = permitRevocationMapper['stoppedDate'].error;
    const stoppedDate = !group.value ? null : group.value.stoppedDate || group.value;

    return moment(stoppedDate).isSameOrBefore(maxDate) || !stoppedDate || stoppedDate?.stoppedDate === null
      ? null
      : {
          [errorProperty]: `The date must be in the past`,
        };
  };
};

export const genericMinDateValidator = (property: string): ValidatorFn => {
  const govukDatePipe = new GovukDatePipe();
  return (group: UntypedFormGroup): ValidationErrors => {
    const minDate = moment(new Date()).set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    const errorProperty = permitRevocationMapper[property].error;
    const fieldValue = !group.value ? null : group.value[property] || group.value;

    return moment(fieldValue).isSameOrAfter(minDate) || !fieldValue
      ? null
      : {
          [errorProperty]: `This date must be the same as or after ${govukDatePipe.transform(
            new Date(minDate.format('YYYY-MM-DD')),
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
