import { RequestDetailsDTO } from 'pmrv-api';

export const reportsTypesMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'Annual improvement': ['AIR'],
    'Determination of activity level': ['DOAL'],
    'Determine emissions': ['DRE'],
    'Emissions report': ['AER'],
    'Verifier improvement': ['VIR'],
  },
  AVIATION: {
    'Annual emissions': ['AVIATION_AER_UKETS', 'AVIATION_AER_CORSIA'],
    'Determine emissions': ['AVIATION_DRE_UKETS'],
    'Verifier improvement': ['AVIATION_VIR'],
  },
};
export const reportsTypesTagsMap: Record<string, any> = {
  AER: 'emissions report',
  AVIATION_AER_UKETS: 'emissions report',
  VIR: 'verifier improvement report',
  AIR: 'annual improvement report',
  DOAL: 'Determination of activity level',
  DRE: 'determination of reportable emissions',
  AVIATION_DRE_UKETS: 'determination of emissions',
  AVIATION_AER_CORSIA: 'emissions report',
  AVIATION_VIR: 'verifier improvement report',
};

export const reportsStatusesMap: Record<string, Partial<Record<RequestDetailsDTO['requestStatus'], string>>> = {
  INSTALLATION: {
    APPROVED: 'Approved',
    CANCELLED: 'Cancelled',
    CLOSED: 'Closed',
    COMPLETED: 'Completed',
    IN_PROGRESS: 'In Progress',
    MIGRATED: 'Migrated',
    REJECTED: 'Rejected',
  },
  AVIATION: {
    IN_PROGRESS: 'In Progress',
    COMPLETED: 'Completed',
    CANCELLED: 'Cancelled',
    EXEMPT: 'Exempt',
  },
};

export const reportsStatusesTagMap: Partial<Record<RequestDetailsDTO['requestStatus'], string>> = {
  IN_PROGRESS: 'IN PROGRESS',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  MIGRATED: 'MIGRATED',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  CLOSED: 'CLOSED',
  EXEMPT: 'EXEMPT',
};
