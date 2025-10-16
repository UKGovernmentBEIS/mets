import { FiltersModel } from '../../../shared/components/permit-batch-reissue/filters.model';

export interface PermitBatchReissueState {
  accountStatuses: FiltersModel['accountStatuses'];
  emitterTypes: FiltersModel['emitterTypes'];
  installationCategories: FiltersModel['installationCategories'];

  changesDetails: object;
  freeAllocation?: boolean;
  nonFreeAllocation?: boolean;
  signatory: string;
}

export const initialState: PermitBatchReissueState = {
  accountStatuses: undefined,
  emitterTypes: undefined,
  installationCategories: undefined,
  freeAllocation: undefined,
  nonFreeAllocation: undefined,

  changesDetails: undefined,
  signatory: undefined,
};
