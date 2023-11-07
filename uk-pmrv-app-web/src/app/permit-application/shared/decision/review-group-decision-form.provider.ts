import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable, of } from 'rxjs';

import { applicableReviewGroupTasks } from '@permit-application/review/utils/review';
import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
import { StatusKey } from '@permit-application/shared/types/permit-task.type';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { transferDetailsStatus } from '@permit-transfer/transfer-status';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { GovukValidators } from 'govuk-components';

import { permitApplicationReviewGroupDecision } from '../types/review.types';
import { reviewGroupTasksCompletedValidator } from './review-group-decision-form.validators';
import { createAnotherRequiredChange } from './review-group-decision-form-utils';

export const REVIEW_GROUP_DECISION_FORM = new InjectionToken<UntypedFormGroup>('Review group decision form');

export const reviewGroupDecisionFormProvider = {
  provide: REVIEW_GROUP_DECISION_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const groupKey: permitApplicationReviewGroupDecision = route.snapshot.data.groupKey;
    const amendsIsNotDisplayed = store.amendsIsNotNeeded(groupKey);
    const reviewDecision = store.reviewGroupDecisions(groupKey);

    const taskStatusPipe = new TaskStatusPipe(store);
    const reviewGroupTasks: string[] = applicableReviewGroupTasks(store.value)[groupKey];
    const reviewGroupTasksStatuses: Observable<TaskItemStatus>[] = [];
    reviewGroupTasks &&
      reviewGroupTasks.forEach((task) => reviewGroupTasksStatuses.push(taskStatusPipe.transform(task as StatusKey)));

    return fb.group(
      {
        decision: [reviewDecision?.type ?? null, GovukValidators.required('Select a decision for this review group')],
        notes: [
          reviewDecision?.details?.notes ?? null,
          [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
        ],
        requiredChanges: fb.array(
          reviewDecision?.details?.requiredChanges?.map((requiredChange: { [key: string]: any }) =>
            createAnotherRequiredChange(store, requestTaskFileService, requiredChange, groupKey),
          ) ?? [createAnotherRequiredChange(store, requestTaskFileService, null, groupKey)],
        ),
      },
      {
        asyncValidators: reviewGroupTasksCompletedValidator(
          amendsIsNotDisplayed
            ? ([of(transferDetailsStatus(store.getState()))] as Observable<TaskItemStatus>[])
            : reviewGroupTasksStatuses,
        ),
        updateOn: 'change',
      },
    );
  },
};
