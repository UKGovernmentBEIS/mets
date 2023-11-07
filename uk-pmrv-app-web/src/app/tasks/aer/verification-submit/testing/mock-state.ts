import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

export function mockStateBuild(value?: any, status?: any): CommonTasksState {
  return {
    ...mockState,
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
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'AER_SAVE_APPLICATION_VERIFICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'AER_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      ...mockVerificationApplyPayload.verificationReport,
      ...value,
      verificationSectionsCompleted: {
        ...mockVerificationApplyPayload.verificationSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
