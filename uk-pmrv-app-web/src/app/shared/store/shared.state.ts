import { PeerReviewDecision } from 'pmrv-api';

export interface SharedState {
  peerReviewDecision?: {
    type?: PeerReviewDecision['type'];
    notes?: string;
  };
}

export const initialState: SharedState = {
  peerReviewDecision: undefined,
};
