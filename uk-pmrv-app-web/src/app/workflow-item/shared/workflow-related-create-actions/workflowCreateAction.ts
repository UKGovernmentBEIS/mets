import {
  AviationAerCorsia3YearPeriodCreateActionPayload,
  AviationAerCorsiaAnnualOffsettingCreateActionPayload,
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
  AVIATION_AER_CORSIA_ANNUAL_OFFSETTING: 'Calculate annual offsetting requirements',
  AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING: 'Calculate 3-year period offsetting requirements',
  BDR: 'Reopen BDR workflow',
  AVIATION_DOE_CORSIA: 'Initiate estimation of emissions',
};

export function createRequestCreateActionProcessDTO(
  requestCreateActionType: RequestCreateActionProcessDTO['requestCreateActionType'],
  requestId: string,
): RequestCreateActionProcessDTO {
  switch (requestCreateActionType) {
    case 'AER':
    case 'DRE':
    case 'AVIATION_DRE_UKETS':
    case 'BDR':
    case 'AVIATION_DOE_CORSIA':
      return {
        requestCreateActionType,
        requestCreateActionPayload: {
          payloadType: 'REPORT_RELATED_REQUEST_CREATE_ACTION_PAYLOAD',
          requestId: requestId,
        } as ReportRelatedRequestCreateActionPayload,
      };
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING':
      return {
        requestCreateActionType,
        requestCreateActionPayload: {
          payloadType: 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_CREATE_ACTION_PAYLOAD',
          requestId: requestId,
        } as AviationAerCorsiaAnnualOffsettingCreateActionPayload,
      };
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING':
      return {
        requestCreateActionType,
        requestCreateActionPayload: {
          payloadType: 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CREATE_ACTION_PAYLOAD',
          requestId: requestId,
        } as AviationAerCorsia3YearPeriodCreateActionPayload,
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
