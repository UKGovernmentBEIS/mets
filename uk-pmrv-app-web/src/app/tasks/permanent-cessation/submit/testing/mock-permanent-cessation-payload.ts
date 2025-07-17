import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { PermanentCessation, PermanentCessationApplicationSubmitRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

export const mockPermanentCessationData: PermanentCessation = {
  description: 'description',
  cessationScope: 'WHOLE_INSTALLATION',
  additionalDetails: 'details',
  cessationDate: '2025-03-28T11:26:49.598473Z',
  regulatorComments: 'Regulator comments',
};

export const mockPermanentCessationApplicationSubmitPayload: PermanentCessationApplicationSubmitRequestTaskPayload = {
  payloadType: 'PERMANENT_CESSATION_SUBMIT_PAYLOAD',
  permanentCessation: mockPermanentCessationData,
  permanentCessationSectionsCompleted: {},
  permanentCessationAttachments: {
    '': '',
  },
};

export const mockPermanentCessationState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['PERMANENT_CESSATION_SAVE_APPLICATION', 'PERMANENT_CESSATION_REQUEST_PEER_REVIEW'],
    userAssignCapable: true,
    requestInfo: {
      id: 'PC00001-1',
      type: 'PERMANENT_CESSATION',
      competentAuthority: 'ENGLAND',
      accountId: 1,
      requestMetadata: {
        type: 'PERMANENT_CESSATION',
        year: '2025',
      },
    },
    requestTask: {
      id: 198,
      type: 'PERMANENT_CESSATION_APPLICATION_SUBMIT',
      payload: {
        payloadType: 'PERMANENT_CESSATION_SUBMIT_PAYLOAD',
        sendEmailNotification: true,
        permanentCessation: {},
        permanentCessationSectionsCompleted: {},
        permanentCessationAttachments: {},
      },
      assignable: true,
      assigneeUserId: '80a57c50-1aaa-421f-9e1d-fdf3268cca8b',
      assigneeFullName: 'Regulator England',
      startDate: '2025-03-13T13:22:04.016857Z',
    } as RequestTaskDTO,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
