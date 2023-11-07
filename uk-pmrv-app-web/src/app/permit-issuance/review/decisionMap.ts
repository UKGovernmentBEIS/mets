import { PermitIssuanceGrantDetermination } from 'pmrv-api';

export function getDecisionNameMap(): Record<PermitIssuanceGrantDetermination['type'], string> {
  return {
    DEEMED_WITHDRAWN: 'Permit application deemed withdrawn',
    GRANTED: 'Permit application approved',
    REJECTED: 'Permit application rejected',
  };
}

export function getDeterminationTypeMap() {
  return {
    DEEMED_WITHDRAWN: 'Deem withdrawn',
    GRANTED: 'Grant',
    REJECTED: 'Reject',
  };
}
