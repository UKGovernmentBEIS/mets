import { InjectionToken } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpRequestTaskPayload,
  ReviewDecisionRequiredChange,
} from 'pmrv-api';

export const FOLLOW_UP_REVIEW_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Review form');

export const followUpReviewDecisionFormProvider = {
  provide: FOLLOW_UP_REVIEW_DECISION_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getState();

    const { reviewDecision } = state.requestTaskItem.requestTask.payload as any;

    return fb.group({
      type: [
        reviewDecision?.type ? reviewDecision?.type : null,
        { validators: GovukValidators.required('Select a decision'), updateOn: 'change' },
      ],
      notes: [
        reviewDecision?.details?.notes ?? null,
        [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      requiredChanges: fb.array(
        reviewDecision?.details?.requiredChanges?.map((requiredChange) =>
          createAnotherRequiredChange(store, requestTaskFileService, requiredChange),
        ) ?? [createAnotherRequiredChange(store, requestTaskFileService, null)],
      ),
      dueDate: [
        {
          value: reviewDecision?.details?.dueDate ? new Date(reviewDecision.details.dueDate) : null,
          disabled: reviewDecision?.type !== 'AMENDS_NEEDED',
        },
        addDueDateValidators(state),
      ],
    });
  },
};

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
        (value as ReviewDecisionRequiredChange)?.files ?? [],
        (store.getValue().requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload)
          ?.followUpAttachments,
        'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
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

function addDueDateValidators(state: CommonTasksState) {
  const taskType = state.requestTaskItem.requestTask.type;
  const payload = state.requestTaskItem.requestTask.payload;
  const isAmends = 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW' !== taskType;
  const dateToCompare = isAmends
    ? (payload as any).reviewDecision?.details?.dueDate
    : (payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload).followUpResponseExpirationDate;

  const dateValidator = dueDateValidator(dateToCompare);

  return isAmends ? [dateValidator, GovukValidators.required('Enter a date')] : [dateValidator];
}

function dueDateValidator(dateToCompare: string): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date()
      ? { invalidDate: `The date must be in the future` }
      : control.value && control.value <= new Date(dateToCompare)
        ? { invalidDate: `The new due date must be after the current due date` }
        : null;
  };
}
