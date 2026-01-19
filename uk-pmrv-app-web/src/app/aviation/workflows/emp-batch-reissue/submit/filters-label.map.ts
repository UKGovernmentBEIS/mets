import { AviationAccountDTO, AviationAccountReportingStatusDTO } from 'pmrv-api';

export const reportingStatusLabelMap: Partial<Record<AviationAccountReportingStatusDTO['status'], string>> = {
  REQUIRED_TO_REPORT: 'Required to report',
  EXEMPT_COMMERCIAL: 'Exempt (commercial)',
  EXEMPT_NON_COMMERCIAL: 'Exempt (non commercial)',
};

export const emissionTradingSchemeLabelMap: Partial<Record<AviationAccountDTO['emissionTradingScheme'], string>> = {
  UK_ETS_AVIATION: 'UK ETS',
  CORSIA: 'CORSIA',
};
