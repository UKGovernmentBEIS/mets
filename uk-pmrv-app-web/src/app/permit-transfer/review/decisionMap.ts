import { RequestActionInfoDTO } from 'pmrv-api';

export function getDecisionNameMap(): Partial<Record<RequestActionInfoDTO['type'], string>> {
  return {
    PERMIT_TRANSFER_A_APPLICATION_GRANTED: 'Permit transfer approved',
    PERMIT_TRANSFER_B_APPLICATION_GRANTED: 'Permit transfer approved',
    PERMIT_TRANSFER_A_APPLICATION_REJECTED: 'Permit transfer rejected',
    PERMIT_TRANSFER_B_APPLICATION_REJECTED: 'Permit transfer rejected',
    PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN: 'Permit transfer deemed withdrawn',
    PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN: 'Permit transfer deemed withdrawn',
  };
}

export function getDeterminationTypeMap() {
  return {
    DEEMED_WITHDRAWN: 'Deem withdrawn',
    GRANTED: 'Grant',
    REJECTED: 'Reject',
  };
}
