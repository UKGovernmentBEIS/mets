export type ReviewGroupDecisionStatus = 'undecided' | 'accepted' | 'rejected' | 'operator to amend' | 'needs review';
export type ReviewGroupTasksAggregatorStatus =
  | 'complete'
  | 'needs review'
  | 'cannot start yet'
  | 'not started'
  | 'in progress'
  | 'undecided';

export type ReviewDeterminationStatus =
  | 'undecided'
  | 'granted'
  | 'rejected'
  | 'deemed withdrawn'
  | 'approved'
  | 'cannot start yet';
