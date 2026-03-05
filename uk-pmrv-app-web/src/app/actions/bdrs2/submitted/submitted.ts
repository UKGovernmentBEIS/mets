import { RequestActionDTO } from 'pmrv-api';

export const getBdrs2ActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  switch (requestActionType) {
    case 'BDRS2_APPLICATION_SENT_TO_VERIFIER':
      return 'Stage 2 baseline data report submitted to verifier';
    case 'BDRS2_APPLICATION_SENT_TO_REGULATOR':
      return 'Stage 2 baseline data report submitted to regulator';
  }
};
