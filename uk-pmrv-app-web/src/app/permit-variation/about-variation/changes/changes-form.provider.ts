import { FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../../store/permit-variation.store';
import {
  nonSignificantChanges,
  significantChangesMonitoringMethodologyPlan,
  significantChangesMonitoringPlan,
} from '../about-variation';

export const changesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitVariationStore],
  useFactory: (fb: FormBuilder, store: PermitVariationStore) => {
    const state = store.getState();
    const modifications = state?.permitVariationDetails?.modifications;

    const changes = modifications?.map((change) => change.type);

    const otherNonSignficant = modifications?.find((change) => change.type === 'OTHER_NON_SIGNFICANT');
    const otherMonitoringPlan = modifications?.find((change) => change.type === 'OTHER_MONITORING_PLAN');
    const otherMonitoringMethodologyPlan = modifications?.find(
      (change) => change.type === 'OTHER_MONITORING_METHODOLOGY_PLAN',
    );

    return fb.group(
      {
        nonSignificantChanges: [
          {
            value: changes?.filter((change) => Object.keys(nonSignificantChanges).includes(change)),
            disabled: !state.isEditable,
          },
        ],
        otherNonSignficantSummary: [
          {
            value: otherNonSignficant?.otherSummary ?? null,
            disabled: !state.isEditable,
          },
          [
            GovukValidators.required('Enter details of the other non-significant changes'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        significantChangesMonitoringPlan: [
          {
            value: changes?.filter((change) => Object.keys(significantChangesMonitoringPlan).includes(change)),
            disabled: !state.isEditable,
          },
        ],
        otherMonitoringPlanSummary: [
          {
            value: otherMonitoringPlan?.otherSummary ?? null,
            disabled: !state.isEditable,
          },
          [
            GovukValidators.required('Enter details of the other significant modifications'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
        significantChangesMonitoringMethodologyPlan: [
          {
            value: changes?.filter((change) =>
              Object.keys(significantChangesMonitoringMethodologyPlan).includes(change),
            ),
            disabled: !state.isEditable,
          },
        ],
        otherMonitoringMethodologyPlanSummary: [
          {
            value: otherMonitoringMethodologyPlan?.otherSummary ?? null,
            disabled: !state.isEditable,
          },
          [
            GovukValidators.required('Enter details of the other significant modifications'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],
      },
      { validators: checkboxSectionsValidator() },
    );
  },
};

const checkboxSectionsValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const isValid =
      group.get('nonSignificantChanges').value?.length ||
      group.get('significantChangesMonitoringPlan').value?.length ||
      group.get('significantChangesMonitoringMethodologyPlan').value?.length;

    return !isValid ? { invalidForm: 'You must select at least one change' } : null;
  };
};
