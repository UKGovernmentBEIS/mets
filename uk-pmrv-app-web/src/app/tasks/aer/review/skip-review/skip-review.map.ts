import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export const skipReviewMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  AER_APPLICATION_REVIEW: 'AER_SKIP_REVIEW',
};
