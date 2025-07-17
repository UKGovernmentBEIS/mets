import { mmpStatuses } from '@permit-application/monitoring-methodology-plan/mmp-status';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey, TaskKey } from '../../shared/types/permit-task.type';
import { resolvePermitSectionStatus } from '../../shared/utils/permit-section-status-resolver';
import { PermitApplicationState } from '../../store/permit-application.state';
import { ReviewDeterminationStatus, ReviewGroupDecisionStatus } from '../types/review.permit.type';
import { mandatoryReviewGroups, mandatoryReviewGroupsTasks, reviewGroupsTasks } from './review.permit';

export function applicableReviewGroupTasks(state: PermitApplicationState) {
  return {
    ...mandatoryReviewGroupsTasks,
    ...(state.permit?.monitoringApproaches?.CALCULATION_CO2
      ? { CALCULATION_CO2: reviewGroupsTasks.CALCULATION_CO2 }
      : {}),
    ...(state.permit?.monitoringApproaches?.MEASUREMENT_CO2
      ? { MEASUREMENT_CO2: reviewGroupsTasks.MEASUREMENT_CO2 }
      : {}),
    ...(state.permit?.monitoringApproaches?.FALLBACK ? { FALLBACK: reviewGroupsTasks.FALLBACK } : {}),
    ...(state.permit?.monitoringApproaches?.MEASUREMENT_N2O
      ? { MEASUREMENT_N2O: reviewGroupsTasks.MEASUREMENT_N2O }
      : {}),
    ...(state.permit?.monitoringApproaches?.CALCULATION_PFC
      ? { CALCULATION_PFC: reviewGroupsTasks.CALCULATION_PFC }
      : {}),
    ...(state.permit?.monitoringApproaches?.INHERENT_CO2 ? { INHERENT_CO2: reviewGroupsTasks.INHERENT_CO2 } : {}),
    ...(state.permit?.monitoringApproaches?.TRANSFERRED_CO2_N2O
      ? { TRANSFERRED_CO2_N2O: reviewGroupsTasks.TRANSFERRED_CO2_N2O }
      : {}),
    MANAGEMENT_PROCEDURES: reviewGroupsTasks.MANAGEMENT_PROCEDURES,
    ...(state.permit?.monitoringMethodologyPlans?.exist && state?.features?.['digitized-mmp']
      ? { MONITORING_METHODOLOGY_PLAN: [...reviewGroupsTasks.MONITORING_METHODOLOGY_PLAN, ...mmpStatuses] }
      : {}),
  };
}

export function findReviewGroupByTaskKey(taskKey: TaskKey): string {
  const key = taskKey.includes('.') ? taskKey.split('.').slice(1, 2).join('.') : taskKey;
  return Object.keys(reviewGroupsTasks).find((reviewGroup) => reviewGroupsTasks[reviewGroup].includes(key));
}

export function resolveReviewGroupStatus(
  state: PermitApplicationState,
  key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
): ReviewGroupDecisionStatus {
  if (!state.isRequestTask) {
    return 'accepted';
  } else {
    const reviewGroupTasks = applicableReviewGroupTasks(state)[key];
    const tasksStatuses: TaskItemStatus[] = [];
    reviewGroupTasks.forEach((task) => tasksStatuses.push(resolvePermitSectionStatus(state, task as StatusKey)));

    if (!tasksStatuses?.length) {
      return 'undecided';
    }

    if (!tasksStatuses?.length || tasksStatuses.includes('in progress')) {
      return 'undecided';
    } else {
      return tasksStatuses.includes('needs review')
        ? 'needs review'
        : tasksStatuses.includes('in progress')
          ? 'undecided'
          : state.reviewSectionsCompleted?.[key] && state.reviewGroupDecisions?.[key]?.type === 'ACCEPTED'
            ? 'accepted'
            : state.reviewSectionsCompleted?.[key] && state.reviewGroupDecisions?.[key]?.type === 'REJECTED'
              ? 'rejected'
              : state.reviewSectionsCompleted?.[key] &&
                  state.reviewGroupDecisions?.[key]?.type === 'OPERATOR_AMENDS_NEEDED'
                ? 'operator to amend'
                : 'undecided'; //fallback
    }
  }
}

export function resolveDeterminationStatus(state: PermitApplicationState): ReviewDeterminationStatus {
  const reviewGroups = mandatoryReviewGroups.concat(
    Object.keys(state.permit.monitoringApproaches ?? {}) as Array<
      PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
    >,
  );

  const areAllReviewGroupsCompleted = reviewGroups.every((mg) => state.reviewSectionsCompleted[mg]);
  return state?.reviewSectionsCompleted?.determination
    ? state?.determination?.type !== 'DEEMED_WITHDRAWN'
      ? areAllReviewGroupsCompleted
        ? state?.determination?.type === 'GRANTED'
          ? 'granted'
          : 'rejected'
        : 'undecided'
      : 'deemed withdrawn'
    : 'undecided';
}
