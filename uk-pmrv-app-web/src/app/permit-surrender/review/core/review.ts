import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { PermitSurrenderReviewDetermination } from 'pmrv-api';

import { PermitSurrenderState } from '../../store/permit-surrender.state';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export type ReviewSectionKey = 'DECISION' | 'DETERMINATION';

export type DecisionStatus = 'undecided' | 'accepted' | 'rejected';
export type DeterminationStatus = 'undecided' | 'granted' | 'rejected' | 'deemed withdrawn' | 'needs review';

export const DeterminationTypeUrlMap: Record<PermitSurrenderReviewDetermination['type'], string> = {
  GRANTED: 'grant',
  REJECTED: 'reject',
  DEEMED_WITHDRAWN: 'deem-withdraw',
};

export function isGrantActionAllowed(state: PermitSurrenderState): boolean {
  return state.reviewDecision?.type === 'ACCEPTED';
}

export function isRejectActionAllowed(state: PermitSurrenderState): boolean {
  return state.reviewDecision?.type === 'REJECTED';
}

export const allowancesBacklinkResolver: ResolveFn<string> = () => {
  const store = inject(PermitSurrenderStore);

  return store.isFinalAlrVisible$.pipe(map((isFinalAlrVisible) => (isFinalAlrVisible ? '../final-alr' : '../report')));
};
