import { AviationAccountDTO, AviationAccountReportingStatusDTO } from 'pmrv-api';

export interface FiltersModel {
  reportingStatuses: AviationAccountReportingStatusDTO['status'][];
  emissionTradingSchemes: AviationAccountDTO['emissionTradingScheme'][];

  numberOfEmitters: number;
}
