import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const alrActivityLevelFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const index = route.snapshot.paramMap.has('index') ? Number(route.snapshot.paramMap.get('index')) : null;

    const activityLevels = (
      state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewOutcome?.activityLevels;

    return fb.group({
      year: [
        { value: index === null ? null : activityLevels[index].year, disabled },
        [GovukValidators.required('Select an option')],
      ],
      subInstallationName: [
        { value: index === null ? null : activityLevels[index].subInstallationName, disabled },
        [GovukValidators.required('Select an option')],
      ],
      changeType: [
        { value: index === null ? null : activityLevels[index].changeType, disabled },
        [GovukValidators.required('Select an option')],
      ],
      otherChangeTypeName: [
        {
          value: index === null ? null : (activityLevels[index]?.otherChangeTypeName ?? null),
          disabled,
        },
        {
          validators: [
            GovukValidators.required('Enter a short name for the change type'),
            GovukValidators.maxLength(255, `Enter up to 255 characters`),
          ],
        },
      ],
      changedActivityLevel: [
        { value: index === null ? null : activityLevels[index].changedActivityLevel, disabled },
        [GovukValidators.maxLength(255, `Enter up to 255 characters`)],
      ],
      comments: [
        { value: index === null ? null : activityLevels[index].comments, disabled },
        {
          validators: [
            GovukValidators.required('Enter a comment'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
    });
  },
};
