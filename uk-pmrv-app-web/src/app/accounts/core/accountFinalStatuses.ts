import { InstallationAccountDTO } from 'pmrv-api';

export function accountFinalStatuses(status: InstallationAccountDTO['status']): boolean {
  return status !== 'UNAPPROVED' && status !== 'DENIED';
}

export function accountFirstYearStatuses(status: InstallationAccountDTO['status']): boolean {
  return (
    status === 'LIVE' ||
    status === 'AWAITING_SURRENDER' ||
    status === 'AWAITING_TRANSFER' ||
    status === 'AWAITING_REVOCATION'
  );
}
