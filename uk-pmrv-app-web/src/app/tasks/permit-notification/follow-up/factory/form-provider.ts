import { InjectionToken } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import moment from 'moment';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

export const PERMIT_NOTIFICATION_FOLLOW_UP_FORM = new InjectionToken<UntypedFormGroup>(
  'Permit notification follow up task form',
);

let followUpResponseExpirationDate = '';

export const permitNotificationFollowUpFormProvider = {
  provide: PERMIT_NOTIFICATION_FOLLOW_UP_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const keys: string[] = route.snapshot.data.keys;
    const skipValidators = route.snapshot.data.skipValidators;
    const formGroupObj = {};

    const state = store.getValue();
    const payload = state.requestTaskItem.requestTask.payload;
    const disabled = !state.isEditable;

    for (const key of keys) {
      if (key) {
        if (key === 'followUpResponseExpirationDate') {
          followUpResponseExpirationDate = payload['followUpResponseExpirationDate'];
        }
        formGroupObj[key] =
          key === 'followUpAttachments'
            ? createFollowUpAttachmentsFormControl(store, requestTaskFileService, false, disabled)
            : [
                {
                  value:
                    payload !== undefined && payload[key] !== undefined && payload[key] !== null
                      ? value(payload, key)
                      : null,
                  disabled,
                },
                { validators: skipValidators ? [] : addValidators(key) },
              ];
      }
    }
    return fb.group(formGroupObj);
  },
};

const value = (payload: any, key: string) => {
  return key.toLowerCase().includes('date') ? new Date(payload[key]) : payload[key];
};

const createFollowUpAttachmentsFormControl = (
  store: CommonTasksStore,
  requestTaskFileService: RequestTaskFileService,
  required: boolean,
  disabled: boolean,
): UntypedFormControl => {
  const state = store.getValue();
  const payload = state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload;
  return requestTaskFileService.buildFormControl(
    store.requestTaskId,
    payload?.followUpFiles ?? [],
    payload?.followUpAttachments,
    'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
    required,
    disabled,
  );
};

// Validators
export const dueDateMinValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    return group.value && group.value < new Date()
      ? { invalidDate: `The date must be in the future` }
      : group.value && group.value <= new Date(followUpResponseExpirationDate)
      ? { invalidDate: `The date must be after ${moment(followUpResponseExpirationDate).format('DD MMM YYYY')}` }
      : null;
  };
};

export const dueDateMinValidatorToday = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const dateAndTime = new Date();
    const today = new Date(dateAndTime.toDateString());

    if (group.value && group.value instanceof Date) {
      const inputValue = new Date(group.value.toDateString());

      if (inputValue < today) {
        return { invalidDate: `The date must be today or in the future` };
      } else if (group.value <= new Date(followUpResponseExpirationDate)) {
        return {
          invalidDate: `The date must be after the ${moment(followUpResponseExpirationDate).format('DD MMM YYYY')}`,
        };
      }
    }
    return null;
  };
};

const addValidators = (key: string): MessageValidatorFn[] => {
  switch (key) {
    case 'followUpResponseExpirationDate':
      return [GovukValidators.required('Enter a date'), dueDateMinValidator()];
    case 'followUpResponse':
      return [
        GovukValidators.required('Enter a response'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ];
    default:
      return null;
  }
};
