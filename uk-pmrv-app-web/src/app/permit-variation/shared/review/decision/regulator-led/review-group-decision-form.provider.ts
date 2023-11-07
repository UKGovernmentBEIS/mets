import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import {
  PermitAcceptedVariationDecisionDetails,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'pmrv-api';

import { applicableReviewGroupTasks } from '../../../../../permit-application/review/utils/review';
import { StatusKey } from '../../../../../permit-application/shared/types/permit-task.type';
import { resolvePermitSectionStatus } from '../../../../../permit-application/shared/utils/permit-section-status-resolver';
import { TaskItemStatus } from '../../../../../shared/task-list/task-list.interface';
import { PermitVariationStore } from '../../../../store/permit-variation.store';
import { variationDetailsStatus } from '../../../../variation-status';
import { AboutVariationGroupKey } from '../../../../variation-types';
import { createAnotherVariationScheduleItem } from '../review-group-decision-form-utils';

export const REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>(
  'Variation regulator led review group decision form',
);

export const reviewGroupDecisionFormProvider = {
  provide: REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, PermitVariationStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: PermitVariationStore, route: ActivatedRoute) => {
    const groupKey = route.snapshot.data.groupKey as
      | PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
      | AboutVariationGroupKey;
    const reviewDecision = store.reviewGroupDecisions(groupKey) as PermitAcceptedVariationDecisionDetails;

    const reviewGroupTasks: string[] = applicableReviewGroupTasks(store.value)[groupKey];
    const reviewGroupTasksStatuses: TaskItemStatus[] = [];
    if (groupKey === 'ABOUT_VARIATION') {
      reviewGroupTasksStatuses.push(variationDetailsStatus(store.getState()));
    } else {
      reviewGroupTasks &&
        reviewGroupTasks.forEach((task) =>
          reviewGroupTasksStatuses.push(resolvePermitSectionStatus(store.getState(), task as StatusKey)),
        );
    }

    return fb.group(
      {
        notes: [reviewDecision?.notes ?? null, [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')]],
        variationScheduleItems: fb.array(
          reviewDecision?.variationScheduleItems?.map((variationScheduleItem) =>
            createAnotherVariationScheduleItem(variationScheduleItem),
          ) ?? [],
        ),
      },
      {
        validators: [sectionCompleted(reviewGroupTasksStatuses)],
        updateOn: 'change',
      },
    );
  },
};

function sectionCompleted(groupTaskStatuses: TaskItemStatus[]): ValidatorFn {
  return GovukValidators.builder('All sections must be completed', () => {
    return groupTaskStatuses.some((status) => status !== 'complete')
      ? { atLeastOne: 'All sections must be completed' }
      : null;
  });
}
