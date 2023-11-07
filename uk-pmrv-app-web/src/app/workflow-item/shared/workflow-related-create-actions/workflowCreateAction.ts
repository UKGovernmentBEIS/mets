import {
  ReportRelatedRequestCreateActionPayload,
  RequestCreateActionEmptyPayload,
  RequestCreateActionProcessDTO,
} from 'pmrv-api';

export const requestCreateActionTypeLabelMap: Partial<
  Record<RequestCreateActionProcessDTO['requestCreateActionType'], string>
> = {
  AER: 'Return to operator for changes',
  DRE: 'Determine reportable emissions',
  AVIATION_DRE_UKETS: 'Determine aviation emissions',
};

export function createRequestCreateActionProcessDTO(
  requestCreateActionType: RequestCreateActionProcessDTO['requestCreateActionType'],
  requestId: string,
): RequestCreateActionProcessDTO {
  switch (requestCreateActionType) {
    case 'AER':
    case 'DRE':
    case 'AVIATION_DRE_UKETS':
      return {
        requestCreateActionType,
        requestCreateActionPayload: {
          payloadType: 'REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD',
          requestId: requestId,
        } as ReportRelatedRequestCreateActionPayload,
      };
    default:
      return {
        requestCreateActionType,
        requestCreateActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestCreateActionEmptyPayload,
      };
  }
}
