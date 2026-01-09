import { RequestDetailsDTO } from 'pmrv-api';

export const reportsTypesMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'Annual improvement': ['AIR'],
    'Activity level report': ['ALR'],
    'Baseline data report': ['BDR'],
    'Determination of activity level': ['DOAL'],
    'Determine emissions': ['DRE'],
    'Emissions report': ['AER'],
    'Verifier improvement': ['VIR'],
  },
  AVIATION: {
    'Annual emissions': ['AVIATION_AER_UKETS', 'AVIATION_AER_CORSIA'],
    'Determine emissions': ['AVIATION_DRE_UKETS'],
    'Verifier improvement': ['AVIATION_VIR'],
    'Calculate annual offsetting requirements': ['AVIATION_AER_CORSIA_ANNUAL_OFFSETTING'],
    'Calculate 3-year offsetting requirements': ['AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING'],
    'Estimate emissions': ['AVIATION_DOE_CORSIA'],
  },
};
export const reportsTypesTagsMap: Record<string, any> = {
  AER: 'emissions report',
  AVIATION_AER_UKETS: 'emissions report',
  VIR: 'verifier improvement report',
  AIR: 'annual improvement report',
  DOAL: 'Determination of activity level',
  DRE: 'determination of reportable emissions',
  BDR: 'baseline data report',
  AVIATION_DRE_UKETS: 'determination of emissions',
  AVIATION_AER_CORSIA: 'emissions report',
  AVIATION_VIR: 'verifier improvement report',
  AVIATION_AER_CORSIA_ANNUAL_OFFSETTING: 'annual offsetting requirements',
  AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING: '3 year offsetting requirements',
  AVIATION_DOE_CORSIA: 'estimation of emissions',
  ALR: 'activity level report',
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
    NOT_REQUIRED: 'Not Required',
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
  NOT_REQUIRED: 'NOT REQUIRED',
};
