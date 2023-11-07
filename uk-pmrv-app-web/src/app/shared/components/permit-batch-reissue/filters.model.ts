import { InstallationAccountDTO } from 'pmrv-api';

export interface FiltersModel {
  accountStatuses: InstallationAccountDTO['status'][];
  emitterTypes: InstallationAccountDTO['emitterType'][];
  installationCategories: InstallationAccountDTO['installationCategory'][];

  numberOfEmitters: number;
}
