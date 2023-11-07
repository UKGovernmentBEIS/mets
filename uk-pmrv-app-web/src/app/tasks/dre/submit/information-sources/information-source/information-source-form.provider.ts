import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const informationSourceFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const index = route.snapshot.paramMap.get('index');

    const informationSources = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
      ?.dre?.informationSources;

    return fb.group({
      informationSource: [
        { value: index === null ? null : informationSources[Number(index)], disabled },
        [
          GovukValidators.required('You must add at least one item'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
