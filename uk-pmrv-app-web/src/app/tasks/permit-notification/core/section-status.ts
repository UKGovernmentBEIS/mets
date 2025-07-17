import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { isAfter } from 'date-fns';

import {
  PermitNotificationApplicationSubmitRequestTaskPayload,
  PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpRequestTaskPayload,
  PermitNotificationReviewDecision,
} from 'pmrv-api';

export type StatusKey =
  | 'DETAILS_CHANGE'
  | 'SUBMIT'
  | 'FOLLOW_UP'
  | 'FOLLOW_UP_SUBMIT'
  | 'REVIEW_FOLLOW_UP'
  | 'FOLLOW_UP_AMENDS'
  | 'FOLLOW_UP_AMENDS_SUBMIT';

export type ReviewSectionKey = 'DETAILS_CHANGE' | 'FOLLOW_UP';

export type DecisionStatus = 'undecided' | 'accepted' | 'rejected' | 'needs review' | 'complete';

export type FollowUpDecisionStatus = 'undecided' | 'accepted' | 'operator to amend' | 'needs review';

export function resolveDetailsChangeStatus(
  state: PermitNotificationApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  return state?.sectionsCompleted['DETAILS_CHANGE']
    ? 'complete'
    : state?.permitNotification
      ? 'in progress'
      : 'not started';
}

export function resolveSubmitStatus(state: PermitNotificationApplicationSubmitRequestTaskPayload): TaskItemStatus {
  return state?.sectionsCompleted['DETAILS_CHANGE'] ? 'not started' : 'cannot start yet';
}

export function isWizardComplete(state: PermitNotificationApplicationSubmitRequestTaskPayload): boolean {
  return state && state?.permitNotification && !!state?.permitNotification?.description;
}

export function isReviewDecisionTakenValid(reviewDecision: PermitNotificationReviewDecision): boolean {
  const validDecisionTakenStatuses: DecisionStatus[] = ['accepted', 'rejected', 'complete'];
  return validDecisionTakenStatuses.includes(resolveReviewDecisionStatus(reviewDecision));
}

export function resolveReviewDecisionStatus(reviewDecision: PermitNotificationReviewDecision): DecisionStatus {
  return !reviewDecision
    ? 'undecided'
    : reviewDecision.type === 'ACCEPTED'
      ? resolveReviewDecisionAcceptedStatus(reviewDecision)
      : ['PERMANENT_CESSATION', 'TEMPORARY_CESSATION', 'CESSATION_TREATED_AS_PERMANENT', 'NOT_CESSATION'].includes(
            reviewDecision.type,
          )
        ? 'complete'
        : 'rejected';
}

export function resolveFollowUpStatus(state: PermitNotificationFollowUpRequestTaskPayload): TaskItemStatus {
  return state?.followUpResponse ? 'complete' : 'not started';
}

export function resolveFollowUpSubmitStatus(state: PermitNotificationFollowUpRequestTaskPayload): TaskItemStatus {
  return resolveFollowUpStatus(state) === 'complete' ? 'not started' : 'cannot start yet';
}

export function resolveFollowUpReviewDecisionStatus(
  payload: PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
): FollowUpDecisionStatus {
  return !payload.reviewDecision?.type || payload.reviewSectionsCompleted?.RESPONSE === false
    ? 'undecided'
    : payload.reviewDecision.type === 'ACCEPTED'
      ? 'accepted'
      : resolveFollowUpReviewDecisionOperatorToAmendStatus(payload);
}

export function isFollowUpReviewDecisionTakenValid(
  taskPayload: PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
): boolean {
  const validDecisionTakenStatuses: FollowUpDecisionStatus[] = ['accepted', 'operator to amend'];
  return validDecisionTakenStatuses.includes(resolveFollowUpReviewDecisionStatus(taskPayload));
}

export function resolveFollowUpDetailsOfAmendsStatus(
  state: PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
): TaskItemStatus {
  return !state?.followUpSectionsCompleted['AMENDS_NEEDED'] ? 'not started' : 'complete';
}

export function resolveFollowUpAmendsSubmitStatus(
  state: PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
): TaskItemStatus {
  return resolveFollowUpStatus(state) === 'complete' && state?.followUpSectionsCompleted['AMENDS_NEEDED']
    ? 'not started'
    : 'cannot start yet';
}

function resolveReviewDecisionAcceptedStatus(reviewDecision: PermitNotificationReviewDecision): DecisionStatus {
  const reviewDecisionDetails = reviewDecision.details as any;
  const followUpResponseExpirationDate = reviewDecisionDetails?.followUp?.followUpResponseExpirationDate;
  return !followUpResponseExpirationDate
    ? 'accepted'
    : isAfter(new Date(), new Date(followUpResponseExpirationDate))
      ? 'needs review'
      : 'accepted';
}

function resolveFollowUpReviewDecisionOperatorToAmendStatus(
  taskPayload: PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
): FollowUpDecisionStatus {
  const followUpResponseNewExpirationDate = (taskPayload?.reviewDecision?.details as any)?.dueDate;
  return isAfter(new Date(), new Date(followUpResponseNewExpirationDate)) ? 'needs review' : 'operator to amend';
}
