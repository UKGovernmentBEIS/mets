import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'pmrv-api';

export function getAerTitle(
  requestActionType: RequestActionDTO['type'],
  payload: AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload,
): string {
  return requestActionType === 'AER_APPLICATION_SUBMITTED' ||
    requestActionType === 'AER_APPLICATION_VERIFICATION_SUBMITTED'
    ? payload.reportingYear + ' emissions report submitted'
    : requestActionType === 'AER_APPLICATION_REVIEW_SKIPPED'
      ? payload.reportingYear + ' completed without review'
      : payload.reportingYear + ' emissions report reviewed';
}
