import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { duplicatePreliminaryAllocationValidator } from '@shared/components/preliminary-allocation-details/preliminary-allocation-details.util';
import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRAuthorityResponseSubmitRequestTaskPayload, ALRGrantAuthorityResponse } from 'pmrv-api';

export const alrPreliminaryAllocationFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const index = route.snapshot.paramMap.has('index') ? Number(route.snapshot.paramMap.get('index')) : null;

    const payload = state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload;
    const preliminaryAllocations =
      (payload?.authorityReviewOutcome?.authorityResponse as ALRGrantAuthorityResponse)?.preliminaryAllocations ?? [];

    return fb.group(
      {
        subInstallationName: [
          { value: index === null ? null : preliminaryAllocations[index].subInstallationName, disabled },
          [GovukValidators.required('Select an option')],
        ],
        year: [
          { value: index === null ? null : preliminaryAllocations[index].year, disabled },
          [GovukValidators.required('Select an option')],
        ],
        allowances: [
          { value: index === null ? null : preliminaryAllocations[index].allowances, disabled },
          [
            GovukValidators.required('Enter a whole number with no decimals, like 1500'),
            GovukValidators.integerNumber(),
          ],
        ],
      },
      {
        validators: duplicatePreliminaryAllocationValidator(preliminaryAllocations, index),
      },
    );
  },
};
