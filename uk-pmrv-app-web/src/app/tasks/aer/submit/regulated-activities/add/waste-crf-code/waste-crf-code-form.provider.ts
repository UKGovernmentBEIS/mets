import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, AerRegulatedActivity } from 'pmrv-api';

import { crfCategories, CrfCategory } from '../crf-codes/crf-codes-item';

export const wasteCrfCodeFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getState();

    const activities =
      (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer?.regulatedActivities ??
      [];
    const activity = activities?.find((activity) => activity.id === route.snapshot.paramMap.get('activityId'));
    const code: AerRegulatedActivity['wasteCrf'] = activity?.wasteCrf ?? null;

    const category: CrfCategory =
      code !== null ? (crfCategories.find((category) => code.startsWith(category)) as CrfCategory) : null;

    const group = fb.group(
      {
        wasteCrfCategory: [category, GovukValidators.required('You must select at least one process')],
        wasteCrf: [code, GovukValidators.required('You must select at least one process')],
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
