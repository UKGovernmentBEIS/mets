import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { activityItemTypeMap } from '../../activity-item';

export const subActivityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const activityItem = route.snapshot.queryParams?.activityItem ?? null;

    const activityError = 'Enter the activity';

    const formGroup = fb.group({
      activity: [
        { value: null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],

      subActivity_2_C: [
        { value: null, disabled: !state.isEditable || activityItem !== '_2' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_2_E: [
        { value: null, disabled: !state.isEditable || activityItem !== '_2' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_3_C: [
        { value: null, disabled: !state.isEditable || activityItem !== '_3' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_4_A: [
        { value: null, disabled: !state.isEditable || activityItem !== '_4' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_4_B: [
        { value: null, disabled: !state.isEditable || activityItem !== '_4' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_7_A: [
        { value: null, disabled: !state.isEditable || activityItem !== '_7' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
      subActivity_8_B: [
        { value: null, disabled: !state.isEditable || activityItem !== '_8' },
        { validators: [GovukValidators.required(activityError), duplicateActivityItemType(store)], updateOn: 'change' },
      ],
    });

    return formGroup;
  },
};

export const duplicateActivityItemType = (store: CommonTasksStore): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors | null => {
    const activityItemType = activityItemTypeMap[group.value];
    const pollutantRegisterActivities = (
      store.getState().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
    ).aer.prtrCodes?.codes;

    return (pollutantRegisterActivities || []).find((x) => x === activityItemType)
      ? { duplicateCode: 'You have already added this activity' }
      : null;
  };
};
