import {
  AviationAerCorsia3YearPeriodCreateActionPayload,
  AviationAerCorsiaAnnualOffsettingCreateActionPayload,
  NERRequestCreateActionPayload,
  ReportRelatedRequestCreateActionPayload,
  RequestCreateActionEmptyPayload,
  RequestCreateActionProcessDTO,
  WithholdingOfAllowancesReCreateActionPayload,
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
  BDRS2: 'Reopen stage 2 BDR workflow',
  AVIATION_DOE_CORSIA: 'Initiate estimation of emissions',
  ALR: 'Mark workflow as not required',
  WITHHOLDING_OF_ALLOWANCES: 'Reopen withhold workflow',
  NER: 'Reopen new entrant reserve workflow',
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
    case 'BDRS2':
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
    case 'WITHHOLDING_OF_ALLOWANCES':
      return {
        requestCreateActionType: 'WITHHOLDING_OF_ALLOWANCES_RE_INITIATE',
        requestCreateActionPayload: {
          payloadType: 'WITHHOLDING_OF_ALLOWANCES_RE_CREATE_ACTION_PAYLOAD',
          requestId,
        } as WithholdingOfAllowancesReCreateActionPayload,
      };
    case 'NER':
      return {
        requestCreateActionType: 'NER_RE_INITIATE',
        requestCreateActionPayload: {
          payloadType: 'NER_CREATE_ACTION_PAYLOAD',
          requestId,
        } as NERRequestCreateActionPayload,
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
