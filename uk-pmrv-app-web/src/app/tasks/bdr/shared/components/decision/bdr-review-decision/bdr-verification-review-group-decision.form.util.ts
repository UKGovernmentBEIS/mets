import { UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const OPINION_STATEMENT = 'OPINION_STATEMENT';
export const OVERALL_DECISION = 'OVERALL_DECISION';

export function createAnotherRequiredChange(
  store: CommonTasksStore,
  requestTaskFileService: RequestTaskFileService,
  value,
): UntypedFormGroup {
  return new UntypedFormGroup(
    {
      reason: new UntypedFormControl(value?.reason ?? null, [
        GovukValidators.required('Enter the change required by the operator'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        value?.files ?? [],
        (store.getValue().requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)
          ?.regulatorReviewAttachments,
        'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
      ),
    },
    {
      validators: [atLeastOneRequiredValidator('You must add an item to the list of changes required.')],
    },
  );
}

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => !!group.controls[key].value) ? null : { atLeastOneRequired: true },
  );
}
