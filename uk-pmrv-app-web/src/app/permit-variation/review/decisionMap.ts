import { PermitVariationGrantDetermination } from 'pmrv-api';

export function getDecisionNameMap(): Partial<Record<PermitVariationGrantDetermination['type'], string>> {
  return {
    DEEMED_WITHDRAWN: 'Permit variation deemed withdrawn',
    GRANTED: 'Permit variation approved',
    REJECTED: 'Permit variation rejected',
  };
}

export function getDeterminationTypeMap() {
  return {
    DEEMED_WITHDRAWN: 'Deem withdrawn',
    GRANTED: 'Approve',
    REJECTED: 'Reject',
  };
}
