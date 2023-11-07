import { RequestDetailsDTO } from 'pmrv-api';

export const statusesTagMap: Record<RequestDetailsDTO['requestStatus'], string> = {
  IN_PROGRESS: 'IN PROGRESS',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  WITHDRAWN: 'WITHDRAWN',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  CLOSED: 'CLOSED',
  MIGRATED: 'MIGRATED',
  EXEMPT: 'EXEMPT',
};
