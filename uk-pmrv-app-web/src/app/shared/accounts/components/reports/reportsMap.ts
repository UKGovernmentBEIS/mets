import { RequestDetailsDTO } from 'pmrv-api';

export const reportsTypesMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'Annual improvement': ['AIR'],
    'Activity level report': ['ALR'],
    'Baseline data report': ['BDR'],
    'Baseline data report (stage 2)': ['BDRS2'],
    'Determination of activity level': ['DOAL'],
    'Determine emissions': ['DRE'],
    'Emissions report': ['AER'],
    'Verifier improvement': ['VIR'],
    'Waste voluntary quarterly report': ['WASTE_QDR'],
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
  BDRS2: 'stage 2 baseline data report',
  AVIATION_DRE_UKETS: 'determination of emissions',
  AVIATION_AER_CORSIA: 'emissions report',
  AVIATION_VIR: 'verifier improvement report',
  AVIATION_AER_CORSIA_ANNUAL_OFFSETTING: 'annual offsetting requirements',
  AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING: '3 year offsetting requirements',
  AVIATION_DOE_CORSIA: 'estimation of emissions',
  ALR: 'activity level report',
  WASTE_QDR: 'quarterly data report',
};

export const reportsStatusesMap: Record<string, Partial<Record<RequestDetailsDTO['requestStatus'], string>>> = {
  INSTALLATION: {
    APPROVED: 'Approved',
    CANCELLED: 'Cancelled',
    CLOSED: 'Closed',
    COMPLETED: 'Completed',
    IN_PROGRESS: 'In progress',
    MIGRATED: 'Migrated',
    REJECTED: 'Rejected',
    NOT_REQUIRED: 'Not required',
  },
  AVIATION: {
    IN_PROGRESS: 'In progress',
    COMPLETED: 'Completed',
    CANCELLED: 'Cancelled',
    EXEMPT: 'Exempt',
  },
};

export const reportsStatusesTagMap: Partial<Record<RequestDetailsDTO['requestStatus'], string>> = {
  IN_PROGRESS: 'In progress',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  MIGRATED: 'Migrated',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
  CLOSED: 'Closed',
  EXEMPT: 'Exempt',
  NOT_REQUIRED: 'Not required',
};
