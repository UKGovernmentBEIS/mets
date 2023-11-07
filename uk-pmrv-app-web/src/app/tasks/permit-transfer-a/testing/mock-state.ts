import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  PermitTransferAApplicationRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

export const mockTransferPayload: PermitTransferAApplicationRequestTaskPayload = {
  reason: 'Reason of transfer',
  reasonAttachments: [],
  transferDate: '2022-11-16',
  payer: 'RECEIVER',
  aerLiable: 'TRANSFERER',
  transferCode: '123456789',
  sectionCompleted: undefined,
  transferAttachments: { '9d9cb990-7cd1-490b-82d0-b0f416e059e1': 'install.txt' },
  payloadType: 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD',
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['PERMIT_TRANSFER_A_SAVE_APPLICATION'],
    requestInfo: {
      id: 'AEMTA53-1',
      type: 'PERMIT_TRANSFER_A',
      competentAuthority: 'ENGLAND',
      accountId: 53,
    },
    requestTask: {
      id: 1,
      type: 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT',
      payload: mockTransferPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockStateBuild(value?: Partial<PermitTransferAApplicationRequestTaskPayload>): CommonTasksState {
  return {
    ...mockState,
    requestTaskItem: {
      ...mockState.requestTaskItem,
      requestTask: {
        ...mockState.requestTaskItem.requestTask,
        payload: {
          ...mockState.requestTaskItem.requestTask.payload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(value?: any): RequestTaskActionProcessDTO {
  const { transferAttachments, ...mockPostPayload } = mockTransferPayload;
  return {
    requestTaskActionType: 'PERMIT_TRANSFER_A_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      ...mockPostPayload,
      ...value,
      payloadType: 'PERMIT_TRANSFER_A_SAVE_APPLICATION_PAYLOAD',
    } as RequestTaskActionPayload,
  };
}
