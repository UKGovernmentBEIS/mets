import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export const recallActionTypeMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  BDR_WAIT_FOR_VERIFICATION: 'BDR_RECALL_FROM_VERIFICATION',
  BDR_AMEND_WAIT_FOR_VERIFICATION: 'BDR_RECALL_FROM_VERIFICATION',
  ALR_WAIT_FOR_VERIFICATION: 'ALR_RECALL_FROM_VERIFICATION',
};

export const recallReturnToTextMap: Partial<Record<RequestTaskDTO['type'], string>> = {
  BDR_WAIT_FOR_VERIFICATION: 'Baseline data report',
  BDR_AMEND_WAIT_FOR_VERIFICATION: 'Baseline data report',
  ALR_WAIT_FOR_VERIFICATION: 'Complete activity level report',
};
