import { FiltersModel } from '@aviation/shared/components/emp-batch-reissue/filters.model';

export interface EmpBatchReissueState {
  reportingStatuses: FiltersModel['reportingStatuses'];
  emissionTradingSchemes: FiltersModel['emissionTradingSchemes'];

  signatory: string;
}

export const initialState: EmpBatchReissueState = {
  reportingStatuses: undefined,
  emissionTradingSchemes: undefined,

  signatory: undefined,
};
