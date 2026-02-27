import { RequestActionDTO } from 'pmrv-api';

export const getBdrs2ActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  switch (requestActionType) {
    case 'BDRS2_APPLICATION_SENT_TO_VERIFIER':
    case 'BDRS2_APPLICATION_AMENDS_SENT_TO_VERIFIER':
      return 'Stage 2 baseline data report submitted to verifier';
    case 'BDRS2_APPLICATION_SENT_TO_REGULATOR':
      return 'Stage 2 baseline data report submitted to regulator';
    case 'BDRS2_VERIFICATION_RETURNED_TO_OPERATOR':
      return 'Stage 2 baseline data report returned to operator for changes';
    case 'BDRS2_APPLICATION_VERIFICATION_SUBMITTED':
      return 'Stage 2 baseline data report verification statement submitted to operator';
    case 'BDRS2_APPLICATION_COMPLETED':
      return 'Stage 2 baseline data report reviewed';
  }
};
