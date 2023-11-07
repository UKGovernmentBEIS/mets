import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationReviewRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockAirApplicationReviewPayload, mockStateReview } from './mock-air-application-review-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockStateReview,
    requestTaskItem: {
      ...mockStateReview.requestTaskItem,
      requestTask: {
        ...mockStateReview.requestTaskItem.requestTask,
        payload: {
          ...mockAirApplicationReviewPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  reviewSectionsCompleted?: AirApplicationReviewRequestTaskPayload['reviewSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'AIR_SAVE_REVIEW',
    requestTaskId: mockStateReview.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'AIR_SAVE_REVIEW_PAYLOAD',
      ...value,
      reviewSectionsCompleted: {
        ...mockAirApplicationReviewPayload.reviewSectionsCompleted,
        ...reviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
