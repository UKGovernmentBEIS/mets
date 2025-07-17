import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRApplicationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

export const mockBDRApplicationSubmitPayload: BDRApplicationSubmitRequestTaskPayload = {
  payloadType: 'BDR_APPLICATION_SUBMIT_PAYLOAD',
  bdrSectionsCompleted: {
    baseline: true,
  },
};

export const mockBDRApplicationSubmitPayloadCompleted: BDRApplicationSubmitRequestTaskPayload = {
  payloadType: 'BDR_APPLICATION_SUBMIT_PAYLOAD',
  bdr: {
    files: ['b6c0615d-cb16-473e-9fe0-d3fa6991e4cf'],
    statusApplicationType: 'NONE',
    isApplicationForFreeAllocation: false,
    infoIsCorrectChecked: true,
    hasMmp: null,
  },
  bdrAttachments: { 'b6c0615d-cb16-473e-9fe0-d3fa6991e4cf': 'test.PNG' },
  bdrSectionsCompleted: {
    baseline: true,
  },
};

export const mockBdrState = {
  requestTaskItem: {
    allowedRequestTaskActions: [],
    requestInfo: {
      id: 'BDR00186-2025',
      type: 'BDR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'BDR',
        year: '2025',
      },
    },
    requestTask: {
      id: 1,
      type: 'BDR_APPLICATION_SUBMIT',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      daysRemaining: -270,
      startDate: '2023-03-15T15:04:23.866188Z',
      payload: mockBDRApplicationSubmitPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
