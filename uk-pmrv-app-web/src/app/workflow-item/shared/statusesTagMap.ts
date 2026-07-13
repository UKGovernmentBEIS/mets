import { RequestDetailsDTO } from 'pmrv-api';

export const statusesTagMap: Record<RequestDetailsDTO['requestStatus'], string> = {
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  WITHDRAWN: 'Withdrawn',
  DEEMED_WITHDRAWN: 'Deemed withdrawn',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
  CLOSED: 'Closed',
  MIGRATED: 'Migrated',
  EXEMPT: 'Exempt',
  NOT_REQUIRED: 'Not required',
};
