import { FiltersModel } from '@aviation/shared/components/emp-batch-reissue/filters.model';

export interface EmpBatchReissueState {
  reportingStatuses: FiltersModel['reportingStatuses'];
  emissionTradingSchemes: FiltersModel['emissionTradingSchemes'];

  changesDetails: object;
  signatory: string;
}

export const initialState: EmpBatchReissueState = {
  reportingStatuses: undefined,
  emissionTradingSchemes: undefined,

  changesDetails: undefined,
  signatory: undefined,
};
