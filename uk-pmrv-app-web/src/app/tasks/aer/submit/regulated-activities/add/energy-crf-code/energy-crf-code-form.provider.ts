import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { crfCategories, CrfCategory } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes-item';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, AerRegulatedActivity } from 'pmrv-api';

export const energyCrfCodeFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getState();

    const activities =
      (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer?.regulatedActivities ??
      [];
    const activity = activities?.find((activity) => activity.id === route.snapshot.paramMap.get('activityId'));
    const code: AerRegulatedActivity['energyCrf'] = activity?.energyCrf ?? null;

    const category: CrfCategory =
      code !== null ? (crfCategories.find((category) => code.startsWith(category)) as CrfCategory) : null;

    const group = fb.group(
      {
        energyCrfCategory: [category, GovukValidators.required('You must select at least one energy type')],
        energyCrf: [code, GovukValidators.required('You must select at least one energy type')],
      },
      {
        updateOn: 'change',
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
