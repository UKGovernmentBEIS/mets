import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { RequestTaskDTO } from 'pmrv-api';

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION',
      'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION',
      'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION',
      'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_UPLOAD_ATTACHMENT',
    ],
    requestInfo: {
      id: 'WA00025-1',
      type: 'WITHHOLDING_OF_ALLOWANCES',
      competentAuthority: 'ENGLAND',
      accountId: 25,
    },
    requestTask: {
      id: 698,
      type: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT',
      payload: {
        payloadType: 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD',
        withholdingWithdrawal: {
          reason: 'Some reason',
        },
        sectionsCompleted: {
          WITHDRAWAL_REASON_CHANGE: false,
        },
      },
      assignable: true,
      assigneeUserId: 'fe1e1f22-18e4-4a62-bf07-91eb239a94d0',
      assigneeFullName: 'r2 Regulator',
      startDate: '2023-07-13T10:58:22.725469Z',
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
