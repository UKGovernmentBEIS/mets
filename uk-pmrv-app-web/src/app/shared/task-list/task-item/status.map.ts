import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
} from '@permit-application/review/types/review.permit.type';

import { TaskItemStatus } from '../task-list.interface';

export const statusMap: Record<
  TaskItemStatus | ReviewGroupDecisionStatus | ReviewDeterminationStatus | 'withdrawn',
  string
> = {
  'not started': 'Not started',
  'cannot start yet': 'Cannot start yet',
  'in progress': 'In progress',
  incomplete: 'Incomplete',
  complete: 'Completed',
  'needs review': 'Needs review',
  undecided: 'Undecided',
  accepted: 'Accepted',
  rejected: 'Rejected',
  'operator to amend': 'Operator to amend',
  granted: 'Granted',
  approved: 'Approved',
  'deemed withdrawn': 'Deemed withdrawn',
  withdrawn: 'Withdrawn',
};
