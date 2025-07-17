import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

import { mockState, mockVerificationApplyPayload } from './mock-bdr-payload';

export function mockStateBuild(value?: any, status?: any, attachments?: any): CommonTasksState {
  return {
    ...mockState,
    isEditable: true,
    requestTaskItem: {
      ...mockState.requestTaskItem,
      requestTask: {
        ...mockState.requestTaskItem.requestTask,
        payload: {
          ...mockVerificationApplyPayload,
          verificationSectionsCompleted: {
            ...mockVerificationApplyPayload.verificationSectionsCompleted,
            ...status,
          },
          verificationReport: {
            ...mockVerificationApplyPayload.verificationReport,
            ...value,
          },
          verificationAttachments: {
            ...attachments,
          },
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'BDR_SAVE_APPLICATION_VERIFICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'BDR_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
      ...mockVerificationApplyPayload.verificationReport,
      ...value,
      verificationSectionsCompleted: {
        ...mockVerificationApplyPayload.verificationSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
