import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRS2ApplicationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

export const mockBDRS2ApplicationSubmitPayload: BDRS2ApplicationSubmitRequestTaskPayload = {
  payloadType: 'BDRS2_APPLICATION_SUBMIT_PAYLOAD',
  bdrs2SectionsCompleted: {
    baseline: true,
  },
};

export const mockBDRS2ApplicationSubmitPayloadCompleted: BDRS2ApplicationSubmitRequestTaskPayload = {
  payloadType: 'BDRS2_APPLICATION_SUBMIT_PAYLOAD',
  bdrs2: {
    files: ['b6c0615d-cb16-473e-9fe0-d3fa6991e4cf'],
    mmpFiles: null,
    bdrs2guardQuestions: {
      continueApplicationForFreeAllocationType: 'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT',
      applicationWithdrawalReason: 'Not needed',
      covidAdjustments: null,
      inEiteSector: null,
    },
  },
  bdrs2Attachments: { 'b6c0615d-cb16-473e-9fe0-d3fa6991e4cf': 'test.PNG' },
  bdrs2SectionsCompleted: {
    baseline: false,
  },
};

export const mockBdrS2State = {
  requestTaskItem: {
    allowedRequestTaskActions: [],
    requestInfo: {
      id: 'BDRS2-00046-2026',
      type: 'BDRS2',
      competentAuthority: 'ENGLAND',
      accountId: 46,
      requestMetadata: {},
    },
    requestTask: {
      id: 1,
      type: 'BDRS2_APPLICATION_SUBMIT',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      daysRemaining: -270,
      startDate: '2023-03-15T15:04:23.866188Z',
      payload: mockBDRS2ApplicationSubmitPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
