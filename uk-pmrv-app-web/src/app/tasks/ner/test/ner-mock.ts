import { RequestTaskDTO } from 'pmrv-api';

export const nerCommonState = {
  requestInfo: {
    id: 'NER00122-4-v1',
    type: 'NER',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'NER',
    },
  },
  requestTask: {
    id: 1,
    assignable: true,
    assigneeFullName: 'Operator name',
    assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
    startDate: '2025-10-13T11:23:36.521308Z',
  } as RequestTaskDTO,
};
