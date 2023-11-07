import { InstallationAccountDTO } from 'pmrv-api';

export function accountFinalStatuses(status: InstallationAccountDTO['status']): boolean {
  return status !== 'UNAPPROVED' && status !== 'DENIED';
}
