import { RequestActionDTO } from 'pmrv-api';

export const getHseTiActionTitle = (requestActionType: RequestActionDTO['type']): string => {
  switch (requestActionType) {
    case 'HSE_TI_APPLICATION_SENT_TO_REGULATOR':
      return 'HSE target increase application submitted to regulator';
    case 'HSE_TI_APPROVED':
      return 'HSE target increase application approved';
    case 'HSE_TI_REJECTED':
      return 'HSE target increase application rejected';
    case 'HSE_TI_WITHDRAWN':
      return 'HSE target increase application withdrawn';
    case 'HSE_TI_DEEMED_WITHDRAWN':
      return 'HSE target increase application deemed withdrawn';
  }
};
