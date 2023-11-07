import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  VirApplicationReviewRequestTaskPayload,
} from 'pmrv-api';

import { mockStateReview, mockVirApplicationReviewPayload } from './mock-vir-application-review-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockStateReview,
    requestTaskItem: {
      ...mockStateReview.requestTaskItem,
      requestTask: {
        ...mockStateReview.requestTaskItem.requestTask,
        payload: {
          ...mockVirApplicationReviewPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  reviewSectionsCompleted?: VirApplicationReviewRequestTaskPayload['reviewSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'VIR_SAVE_REVIEW',
    requestTaskId: mockStateReview.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'VIR_SAVE_REVIEW_PAYLOAD',
      ...value,
      reviewSectionsCompleted: {
        ...mockVirApplicationReviewPayload.reviewSectionsCompleted,
        ...reviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
