import { InjectionToken } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CommonTasksStore } from '../../../store/common-tasks.store';

export const REVIEW_FORM = new InjectionToken<UntypedFormGroup>('Review form');

export const decisionFormProvider = {
  provide: REVIEW_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getState();

    const { reviewDecision, permitNotification } = state.requestTaskItem.requestTask.payload as any;

    const formGroup = fb.group({
      type: [
        permitNotification?.type === 'CESSATION'
          ? reviewDecision?.type
          : reviewDecision?.type === 'ACCEPTED'
            ? true
            : reviewDecision?.type === 'REJECTED'
              ? false
              : null,
        { validators: GovukValidators.required('Select a decision'), updateOn: 'change' },
      ],
      officialNotice: [
        reviewDecision?.details?.officialNotice ?? null,
        [GovukValidators.required('Enter a comment'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],

      notes: [reviewDecision?.details?.notes ?? null, GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ...(reviewDecision?.type !== false
        ? {
            followUpResponseRequired: [
              reviewDecision?.details?.followUp?.followUpResponseRequired ?? null,
              {
                validators: GovukValidators.required('Select yes or no'),
              },
            ],
          }
        : {}),
      ...(reviewDecision?.type !== false
        ? {
            followUpRequest: [
              reviewDecision?.details?.followUp?.followUpRequest ?? null,
              [
                GovukValidators.required('Enter details'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ],
          }
        : {}),
      ...(reviewDecision?.type !== false
        ? {
            followUpResponseExpirationDate: [
              reviewDecision?.details?.followUp?.followUpResponseExpirationDate
                ? new Date(reviewDecision?.details?.followUp?.followUpResponseExpirationDate)
                : null,
              [futureDateValidator()],
            ],
          }
        : {}),
    });

    formGroup.controls?.type.valueChanges.subscribe((value) => {
      if (value !== 'NOT_CESSATION') {
        if (!formGroup.contains('followUpResponseRequired')) {
          formGroup.addControl(
            'followUpResponseRequired',
            fb.control(reviewDecision?.details?.followUp?.followUpResponseRequired ?? null, {
              validators: GovukValidators.required('Select yes or no'),
            }),
          );
        }
        if (!formGroup.contains('followUpRequest')) {
          formGroup.addControl(
            'followUpRequest',
            fb.control(reviewDecision?.details?.followUp?.followUpRequest ?? null, [
              GovukValidators.required('Enter details'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );
        }
        if (!formGroup.contains('followUpResponseExpirationDate')) {
          formGroup.addControl(
            'followUpResponseExpirationDate',
            fb.control(
              reviewDecision?.details?.followUp?.followUpResponseExpirationDate
                ? new Date(reviewDecision?.details?.followUp?.followUpResponseExpirationDate)
                : null,
              [futureDateValidator()],
            ),
          );
        }

        formGroup.controls?.followUpResponseRequired.updateValueAndValidity({ emitEvent: true });
        formGroup.controls?.followUpRequest?.updateValueAndValidity({ emitEvent: true });
        formGroup.controls?.followUpResponseExpirationDate?.updateValueAndValidity({ emitEvent: true });
      } else {
        formGroup.removeControl('followUpResponseRequired');
        formGroup.removeControl('followUpRequest');
        formGroup.removeControl('followUpResponseExpirationDate');
      }
    });

    return formGroup;
  },
};

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date() ? { invalidDate: `The date must be in the future` } : null;
  };
}
