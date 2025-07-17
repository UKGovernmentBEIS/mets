import { RequestActionDTO } from 'pmrv-api';

export const getBdrActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  switch (requestActionType) {
    case 'BDR_APPLICATION_SENT_TO_VERIFIER':
    case 'BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER':
      return 'Baseline data report submitted to verifier';
    case 'BDR_APPLICATION_SENT_TO_REGULATOR':
      return 'Baseline data report submitted to regulator';
    case 'BDR_APPLICATION_VERIFICATION_SUBMITTED':
      return 'Baseline data report verification statement submitted to operator';
    case 'BDR_VERIFICATION_RETURNED_TO_OPERATOR':
      return 'Baseline data report returned to operator for changes';
    case 'BDR_APPLICATION_COMPLETED':
      return 'Baseline data report reviewed';
  }
};
