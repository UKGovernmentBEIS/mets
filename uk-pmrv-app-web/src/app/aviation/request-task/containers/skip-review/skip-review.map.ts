import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export const skipReviewMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  AVIATION_AER_UKETS_APPLICATION_REVIEW: 'AVIATION_AER_UKETS_SKIP_REVIEW',
};
