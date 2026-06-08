import { UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ReviewDecisionPayload } from '@shared/types';

import { GovukValidators } from 'govuk-components';

import { RequestTaskAttachmentActionProcessDTO, RequestTaskPayload } from 'pmrv-api';

export const createAnotherRequiredChange = (
  requestTaskId: number,
  regulatorReviewAttachments: ReviewDecisionPayload['regulatorReviewAttachments'],
  requestTaskFileService: RequestTaskFileService,
  payloadType: RequestTaskPayload['payloadType'],
  value: any,
): UntypedFormGroup => {
  return new UntypedFormGroup(
    {
      reason: new UntypedFormControl(value?.reason ?? null, [
        GovukValidators.required('Enter the change required by the operator'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
      files: requestTaskFileService.buildFormControl(
        requestTaskId,
        value?.files ?? [],
        regulatorReviewAttachments,
        getTaskActionType(payloadType),
        false,
      ),
    },
    {
      validators: [atLeastOneRequiredValidator('You must add an item to the list of changes required.')],
    },
  );
};

const atLeastOneRequiredValidator = (message: string): ValidatorFn => {
  return GovukValidators.builder(message, (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => !!group.controls[key].value) ? null : { atLeastOneRequired: true },
  );
};

const getTaskActionType = (
  payloadType: RequestTaskPayload['payloadType'],
): RequestTaskAttachmentActionProcessDTO['requestTaskActionType'] => {
  switch (payloadType) {
    case 'ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
      return 'ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT';
    case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
      return 'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT';
    case 'NER_APPLICATION_REVIEW_PAYLOAD':
      return 'NER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT';
    case 'WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
      return 'WASTE_QDR_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT';
  }
};

export const constructReviewDecision = (form: UntypedFormGroup): any => {
  return {
    type: form.controls.decision.value,
    details: {
      ...(form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED' && !!form.controls.verificationRequired
        ? { verificationRequired: form.controls.verificationRequired.value }
        : {}),
      notes: form.controls.notes.value,
      ...(form.controls.decision.value === 'OPERATOR_AMENDS_NEEDED'
        ? {
            requiredChanges: (form.controls.requiredChanges.value as Array<any>).map((requiredChange) => ({
              reason: requiredChange.reason,
              files: requiredChange.files.map((file: any) => file.uuid),
            })),
          }
        : {}),
    },
  };
};

export const getFileListTitle = (requestType: ReviewDecisionPayload['payloadType']) => {
  switch (requestType) {
    case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
      return 'Uploaded attachments for the operator';

    default:
      return 'Uploaded files';
  }
};

export const taskHasNoVerification = (payloadType: RequestTaskPayload['payloadType']): boolean => {
  switch (payloadType) {
    case 'WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD':
      return true;

    default:
      return false;
  }
};
