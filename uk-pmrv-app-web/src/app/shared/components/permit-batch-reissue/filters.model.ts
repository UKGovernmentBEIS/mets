import { InstallationAccountDTO } from 'pmrv-api';

export interface FiltersModel {
  accountStatuses: InstallationAccountDTO['status'][];
  emitterTypes: InstallationAccountDTO['emitterType'][];
  installationCategories: InstallationAccountDTO['installationCategory'][];
  freeAllocation: boolean;
  nonFreeAllocation: boolean;
  numberOfEmitters: number;
}
