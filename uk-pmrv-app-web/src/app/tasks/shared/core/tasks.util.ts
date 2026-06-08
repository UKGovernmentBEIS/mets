import { RequestTaskActionPayload, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export const getWorkflowTypeText = (requestTaskType: RequestTaskDTO['type']): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'application';
    default:
      return 'report';
  }
};

export const getRequestTaskActionType = (
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'NER_VERIFICATION_RETURN_TO_OPERATOR';
    case 'ALR_APPLICATION_VERIFICATION_SUBMIT':
    case 'ALR_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'ALR_VERIFICATION_RETURN_TO_OPERATOR';
    case 'BDR_APPLICATION_VERIFICATION_SUBMIT':
    case 'BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'BDR_VERIFICATION_RETURN_TO_OPERATOR';
    case 'BDRS2_APPLICATION_VERIFICATION_SUBMIT':
    case 'BDRS2_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'BDRS2_VERIFICATION_RETURN_TO_OPERATOR';

    default:
      return null;
  }
};

export const createRequestTaskActionPayload = (
  actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  changesRequired: string,
) => {
  return { payloadType: `${actionType}_PAYLOAD`, changesRequired: changesRequired } as RequestTaskActionPayload;
};
