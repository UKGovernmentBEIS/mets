import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

import {
  mockPermitCompletedReviewSectionsCompleted,
  mockPermitCompletePayload,
} from '../../permit-application/testing/mock-permit-apply-action';
import { mockState } from '../../permit-application/testing/mock-state';
import { PermitVariationState } from '../store/permit-variation.state';

export const mockPermitVariationSubmitOperatorLedPayload: PermitVariationState = {
  ...mockState,
  ...mockPermitCompletePayload,
  reviewSectionsCompleted: mockPermitCompletedReviewSectionsCompleted,
  payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
  requestTaskType: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
};

export const mockPermitVariationReviewOperatorLedPayload: PermitVariationState = {
  ...mockState,
  ...mockPermitCompletePayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  requestTaskType: 'PERMIT_VARIATION_APPLICATION_REVIEW',
  payloadType: 'PERMIT_VARIATION_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  assignable: true,
  permitVariationDetailsCompleted: undefined,
  permitVariationDetailsReviewDecision: undefined,
  permitVariationDetailsReviewCompleted: undefined,
  determination: undefined,
  requestType: 'PERMIT_VARIATION',
};

export const mockPermitVariationCompleteAcceptedReviewGroupDecisions = {
  PERMIT_TYPE: { type: 'ACCEPTED', variationScheduleItems: [] },
  ADDITIONAL_INFORMATION: { type: 'ACCEPTED', variationScheduleItems: [] },
  CONFIDENTIALITY_STATEMENT: { type: 'ACCEPTED', variationScheduleItems: [] },
  DEFINE_MONITORING_APPROACHES: { type: 'ACCEPTED', variationScheduleItems: [] },
  FUELS_AND_EQUIPMENT: { type: 'ACCEPTED', variationScheduleItems: [] },
  INSTALLATION_DETAILS: { type: 'ACCEPTED', variationScheduleItems: [] },
  MANAGEMENT_PROCEDURES: { type: 'ACCEPTED', variationScheduleItems: [] },
  MEASUREMENT_CO2: { type: 'ACCEPTED', variationScheduleItems: [] },
  MONITORING_METHODOLOGY_PLAN: { type: 'ACCEPTED', variationScheduleItems: [] },
  MEASUREMENT_N2O: { type: 'ACCEPTED', variationScheduleItems: [] },
  CALCULATION_PFC: { type: 'ACCEPTED', variationScheduleItems: [] },
  FALLBACK: { type: 'ACCEPTED', variationScheduleItems: [] },
  TRANSFERRED_CO2_N2O: { type: 'ACCEPTED', variationScheduleItems: [] },
  INHERENT_CO2: { type: 'ACCEPTED', variationScheduleItems: [] },
  UNCERTAINTY_ANALYSIS: { type: 'ACCEPTED', variationScheduleItems: [] },
};

export const mockPermitVariationRegulatorLedPayload: PermitVariationState = {
  ...mockState,
  ...mockPermitCompletePayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  requestTaskType: 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
  payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  assignable: true,
  permitVariationDetailsCompleted: undefined,
  permitVariationDetailsReviewDecision: undefined,
  permitVariationDetailsReviewCompleted: undefined,
  determination: undefined,
  requestType: 'PERMIT_VARIATION',
};

export function mockVariationDeterminationPostBuild(value?, status?): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION',
    requestTaskId: mockPermitVariationReviewOperatorLedPayload.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination: value,
      reviewSectionsCompleted: {
        ...mockPermitVariationReviewOperatorLedPayload.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockVariationRegulatorLedDeterminationPostBuild(value?, status?): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED',
    requestTaskId: mockPermitVariationRegulatorLedPayload.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED_PAYLOAD',
      determination: value,
      reviewSectionsCompleted: {
        ...mockPermitVariationRegulatorLedPayload.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
