import { UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { permitApplicationReviewGroupDecision } from '../types/review.types';

export function createAnotherRequiredChange(
  store: PermitApplicationStore<PermitApplicationState>,
  requestTaskFileService: RequestTaskFileService,
  value: { [key: string]: any },
  groupKey?: permitApplicationReviewGroupDecision,
): UntypedFormGroup {
  return new UntypedFormGroup(
    {
      reason: new UntypedFormControl(
        value?.reason ?? null,
        store.amendsIsNotNeeded(groupKey)
          ? null
          : [
              GovukValidators.required('Enter the change required by the operator'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ],
      ),
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        value?.files ?? [],
        store.getValue()?.reviewAttachments,
        store.getFileUploadReviewGroupDecisionAttachmentActionContext(),
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
