import { AviationAccountDTO } from 'pmrv-api';

export interface FiltersModel {
  reportingStatuses: AviationAccountDTO['reportingStatus'][];
  emissionTradingSchemes: AviationAccountDTO['emissionTradingScheme'][];

  numberOfEmitters: number;
}
