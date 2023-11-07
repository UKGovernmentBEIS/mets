import { FiltersModel } from '../../../shared/components/permit-batch-reissue/filters.model';

export interface PermitBatchReissueState {
  accountStatuses: FiltersModel['accountStatuses'];
  emitterTypes: FiltersModel['emitterTypes'];
  installationCategories: FiltersModel['installationCategories'];

  signatory: string;
}

export const initialState: PermitBatchReissueState = {
  accountStatuses: undefined,
  emitterTypes: undefined,
  installationCategories: undefined,

  signatory: undefined,
};
