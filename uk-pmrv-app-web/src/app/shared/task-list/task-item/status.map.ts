import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
} from '../../../permit-application/review/types/review.permit.type';
import { TaskItemStatus } from '../task-list.interface';

export const statusMap: Record<
  TaskItemStatus | ReviewGroupDecisionStatus | ReviewDeterminationStatus | 'withdrawn',
  string
> = {
  'not started': 'not started',
  'cannot start yet': 'cannot start yet',
  'in progress': 'in progress',
  incomplete: 'incomplete',
  complete: 'completed',
  'needs review': 'needs review',
  undecided: 'undecided',
  accepted: 'accepted',
  rejected: 'rejected',
  'operator to amend': 'operator to amend',
  granted: 'granted',
  approved: 'approved',
  'deemed withdrawn': 'deemed withdrawn',
  withdrawn: 'withdrawn',
};
