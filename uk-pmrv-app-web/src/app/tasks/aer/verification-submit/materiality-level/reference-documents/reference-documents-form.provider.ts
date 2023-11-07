import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

import {
  euEtsVerificationConductAccreditedVerifiers,
  euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders,
  euEtsVerificationRules,
  ukEtsVerificationConductAccreditedVerifiers,
  ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders,
  ukEtsVerificationRules,
} from './reference-document-types';

export const referenceDocumentsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const materialityLevelInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.materialityLevel;

    const types = materialityLevelInfo?.accreditationReferenceDocumentTypes;

    return fb.group(
      {
        ukEtsVerificationConductAccreditedVerifiers: [
          {
            value: types?.filter((type) => ukEtsVerificationConductAccreditedVerifiers.includes(type)),
            disabled: !state.isEditable,
          },
        ],
        ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders: [
          {
            value: types?.filter((type) =>
              ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders.includes(type),
            ),
            disabled: !state.isEditable,
          },
        ],
        ukEtsVerificationRules: [
          {
            value: types?.filter((type) => ukEtsVerificationRules.includes(type)),
            disabled: !state.isEditable,
          },
        ],
        euEtsVerificationConductAccreditedVerifiers: [
          {
            value: types?.filter((type) => euEtsVerificationConductAccreditedVerifiers.includes(type)),
            disabled: !state.isEditable,
          },
        ],
        euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders: [
          {
            value: types?.filter((type) =>
              euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders.includes(type),
            ),
            disabled: !state.isEditable,
          },
        ],
        euEtsVerificationRules: [
          {
            value: types?.filter((type) => euEtsVerificationRules.includes(type)),
            disabled: !state.isEditable,
          },
        ],

        otherReferences: [
          {
            value: types?.filter((type) => 'OTHER' === type),
            disabled: !state.isEditable,
          },
        ],
        otherReference: [
          { value: materialityLevelInfo?.otherReference ?? null, disabled: !state.isEditable },
          {
            validators: [
              GovukValidators.required(`Enter details`),
              GovukValidators.maxLength(10000, `The details should not be more than 10000 characters`),
            ],
          },
        ],
      },
      { validators: atLeastOneTypeSelectedValidator() },
    );
  },
};

const atLeastOneTypeSelectedValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const isValid =
      group.get('ukEtsVerificationConductAccreditedVerifiers').value?.length ||
      group.get('ukEtsVerificationConductAccreditedVerifiersAndAssuranceProviders').value?.length ||
      group.get('ukEtsVerificationRules').value?.length ||
      group.get('euEtsVerificationConductAccreditedVerifiers').value?.length ||
      group.get('euEtsVerificationConductAccreditedVerifiersAndAssuranceProviders').value?.length ||
      group.get('euEtsVerificationRules').value?.length ||
      group.get('otherReferences').value?.length;
    return !isValid ? { invalidForm: 'Please select at least one reference document from the list' } : null;
  };
};
