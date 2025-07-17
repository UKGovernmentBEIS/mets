import { addDays, isAfter, startOfDay } from 'date-fns';

import { PermitSurrenderReviewDecision } from 'pmrv-api';

import { PermitSurrenderState } from '../../store/permit-surrender.state';
import { DecisionStatus, DeterminationStatus } from './review';

export function resolveDecisionStatus(state: PermitSurrenderState): DecisionStatus {
  return !state.reviewDecision ? 'undecided' : state.reviewDecision.type === 'ACCEPTED' ? 'accepted' : 'rejected';
}

export function resolveDeterminationStatus(state: PermitSurrenderState): DeterminationStatus {
  const reviewDeterminationCompleted = state.reviewDeterminationCompleted;
  if (!reviewDeterminationCompleted) {
    return needsReview(state) ? 'needs review' : 'undecided';
  }

  const reviewDeterminationType = state.reviewDetermination?.type;
  switch (reviewDeterminationType) {
    case 'GRANTED':
      return needsReview(state) ? 'needs review' : 'granted';
    case 'REJECTED':
      return 'rejected';
    case 'DEEMED_WITHDRAWN':
      return 'deemed withdrawn';
    default:
      return needsReview(state) ? 'needs review' : 'undecided';
  }
}

export const needsReview = (state) => {
  const noticeDate = state.reviewDetermination?.noticeDate;
  if (!noticeDate) {
    return false;
  }
  const after28Days = startOfDay(addDays(new Date(), 28));

  return !isAfter(noticeDate, after28Days);
};

export function resolveDeterminationCompletedUponDecision(
  newType: PermitSurrenderReviewDecision['type'],
  state: PermitSurrenderState,
): boolean {
  const reviewDeterminationType = state.reviewDetermination?.type;
  const reviewDeterminationCompleted = state.reviewDeterminationCompleted;

  if (!reviewDeterminationType) {
    return null;
  }

  if (reviewDeterminationType === 'DEEMED_WITHDRAWN') {
    return reviewDeterminationCompleted;
  }

  switch (newType) {
    case 'ACCEPTED':
      return reviewDeterminationType === 'GRANTED';
    case 'REJECTED':
      return reviewDeterminationType === 'REJECTED';
  }
}
