import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const complianceMonitoringFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const noSelection = 'Please select yes or no';
    const noReason = 'Please provide a reason';
    const maxReason = 'The reason should not be more than 10000 characters';

    const complianceMonitoring = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.complianceMonitoringReporting;

    return fb.group({
      accuracy: [
        { value: complianceMonitoring?.accuracy ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      accuracyReason: [
        { value: complianceMonitoring?.accuracyReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
      completeness: [
        { value: complianceMonitoring?.completeness ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSelection)],
        },
      ],
      completenessReason: [
        { value: complianceMonitoring?.completenessReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
      consistency: [
        { value: complianceMonitoring?.consistency ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSelection)],
        },
      ],
      consistencyReason: [
        { value: complianceMonitoring?.consistencyReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
      comparability: [
        { value: complianceMonitoring?.comparability ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSelection)],
        },
      ],
      comparabilityReason: [
        { value: complianceMonitoring?.comparabilityReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
      transparency: [
        { value: complianceMonitoring?.transparency ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSelection)],
        },
      ],
      transparencyReason: [
        { value: complianceMonitoring?.transparencyReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
      integrity: [
        { value: complianceMonitoring?.integrity ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noSelection)],
        },
      ],
      integrityReason: [
        { value: complianceMonitoring?.integrityReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
    });
  },
};
