import {
  NERApplicationAmendsSubmitRequestTaskPayload,
  NERApplicationRegulatorReviewSubmitRequestTaskPayload,
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

export type NerPayload = NerApplicationSubmitRequestTaskPayload &
  NERApplicationVerificationSubmitRequestTaskPayload &
  NERApplicationRegulatorReviewSubmitRequestTaskPayload &
  NERApplicationAmendsSubmitRequestTaskPayload;

export const nerDetailsCaption = (requestTaskType: RequestTaskDTO['type'], isOutcomeSubtask: boolean): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return 'New entrant reserve';
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return null;
    case 'NER_APPLICATION_REVIEW':
    case 'NER_APPLICATION_PEER_REVIEW':
      return isOutcomeSubtask ? 'Outcome of regulator review' : 'New entrant reserve';

    default:
      return null;
  }
};

export const nerDetailsHeading = (requestTaskType: RequestTaskDTO['type'], isOutcomeSubtask: boolean): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return 'Check your answers';
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'New entrant reserve';
    case 'NER_APPLICATION_REVIEW':
    case 'NER_APPLICATION_PEER_REVIEW':
      return isOutcomeSubtask
        ? 'What is your decision on the new entrant reserve application?'
        : 'Review the new entrant reserve details';

    default:
      return null;
  }
};

export const nerOpinionStatementHeading = (requestTaskType: RequestTaskDTO['type']): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'Upload new entrant reserve verification opinion statement';
    case 'NER_APPLICATION_REVIEW':
    case 'NER_APPLICATION_PEER_REVIEW':
      return 'Review the new entrant reserve verification opinion statement';

    default:
      return null;
  }
};

export const nerOverallDecisionHeading = (requestTaskType: RequestTaskDTO['type']): string => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return 'Check your answers';
    case 'NER_APPLICATION_REVIEW':
    case 'NER_APPLICATION_PEER_REVIEW':
      return 'Review the overall decision';

    default:
      return null;
  }
};

export const nerReturnLinkLevelsUp = (requestTaskType: RequestTaskDTO['type'], section?: 'NER' | 'OUTCOME'): number => {
  switch (requestTaskType) {
    case 'NER_APPLICATION_SUBMIT':
    case 'NER_APPLICATION_AMENDS_SUBMIT':
      return 2;
    case 'NER_APPLICATION_VERIFICATION_SUBMIT':
    case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      return section === 'NER' ? 1 : 2;
    case 'NER_APPLICATION_REVIEW':
    case 'NER_APPLICATION_PEER_REVIEW':
      return section === 'OUTCOME' ? 2 : 1;

    default:
      return 1;
  }
};

export const nerDetailsDataIsEditable = (requestType: RequestTaskDTO['type'], isEditable: boolean): boolean => {
  return requestType === 'NER_APPLICATION_SUBMIT' || requestType === 'NER_APPLICATION_AMENDS_SUBMIT'
    ? isEditable
    : false;
};

export const nerVerificationDataIsEditable = (requestType: RequestTaskDTO['type'], isEditable: boolean): boolean => {
  return requestType === 'NER_APPLICATION_VERIFICATION_SUBMIT' ||
    requestType === 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT'
    ? isEditable
    : false;
};

export const nerReviewTasks: Array<RequestTaskDTO['type']> = ['NER_APPLICATION_REVIEW', 'NER_APPLICATION_PEER_REVIEW'];
